package com.salesnotes.service;

import com.salesnotes.dto.*;
import com.salesnotes.entity.*;
import com.salesnotes.repository.*;
import com.salesnotes.security.UserPrincipal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional
public class CatalogueService {

    private final CatalogueItemRepository catalogueItemRepository;
    private final ItemPriceTierRepository tierRepository;
    private final ItemAttachmentRepository attachmentRepository;
    private final ItemChangeLogRepository changeLogRepository;
    private final OrganisationRepository organisationRepository;
    private final UserRepository userRepository;

    public CatalogueService(
            CatalogueItemRepository catalogueItemRepository,
            ItemPriceTierRepository tierRepository,
            ItemAttachmentRepository attachmentRepository,
            ItemChangeLogRepository changeLogRepository,
            OrganisationRepository organisationRepository,
            UserRepository userRepository
    ) {
        this.catalogueItemRepository = catalogueItemRepository;
        this.tierRepository = tierRepository;
        this.attachmentRepository = attachmentRepository;
        this.changeLogRepository = changeLogRepository;
        this.organisationRepository = organisationRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public Page<CatalogueItemResponseDto> listItems(UserPrincipal currentUser, String category, String search, Pageable pageable) {
        String orgId = currentUser.getOrganisationId();
        List<CatalogueItem> items;
        if (orgId != null) {
            items = catalogueItemRepository.findByOrganisationIdAndActiveTrueAndDeletedFalse(orgId);
        } else {
            items = catalogueItemRepository.findAll().stream()
                    .filter(i -> Boolean.TRUE.equals(i.getActive()) && !Boolean.TRUE.equals(i.getDeleted()))
                    .collect(Collectors.toList());
        }

        List<CatalogueItemResponseDto> filtered = items.stream()
                .filter(i -> {
                    if (category == null || category.trim().isEmpty()) return true;
                    return i.getCategory() != null && category.equalsIgnoreCase(i.getCategory().trim());
                })
                .filter(i -> {
                    if (search == null || search.trim().isEmpty()) return true;
                    String term = search.trim().toLowerCase();
                    boolean nameMatch = i.getItemName() != null && i.getItemName().toLowerCase().contains(term);
                    boolean codeMatch = i.getItemCode() != null && i.getItemCode().toLowerCase().contains(term);
                    return nameMatch || codeMatch;
                })
                .map(CatalogueItemResponseDto::fromEntity)
                .collect(Collectors.toList());

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), filtered.size());
        List<CatalogueItemResponseDto> pageContent = start <= end ? filtered.subList(start, end) : List.of();

        return new PageImpl<>(pageContent, pageable, filtered.size());
    }

    @Transactional(readOnly = true)
    public CatalogueItemResponseDto getItemById(String id, UserPrincipal currentUser) {
        CatalogueItem item = catalogueItemRepository.findById(id)
                .filter(i -> !Boolean.TRUE.equals(i.getDeleted()))
                .orElseThrow(() -> new IllegalArgumentException("Catalogue item not found: " + id));

        return CatalogueItemResponseDto.fromEntity(item);
    }

    public CatalogueItemResponseDto createItem(CatalogueItemRequestDto request, UserPrincipal currentUser) {
        Organisation org = null;
        if (currentUser.getOrganisationId() != null) {
            org = organisationRepository.findById(currentUser.getOrganisationId()).orElse(null);
        }
        if (org == null) {
            org = organisationRepository.findAll().stream().findFirst()
                    .orElseThrow(() -> new IllegalStateException("Organisation not found"));
        }

        String sku = request.getItemCode().trim().toUpperCase();
        if (catalogueItemRepository.findByOrganisationIdAndItemCodeAndDeletedFalse(org.getId(), sku).isPresent()) {
            throw new IllegalArgumentException("SKU / Item code already exists: " + sku);
        }

        User adminUser = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new IllegalStateException("Admin user not found: " + currentUser.getId()));

        CatalogueItem item = new CatalogueItem(
                org,
                adminUser,
                sku,
                request.getItemName().trim(),
                request.getUom().trim(),
                request.getBasePrice()
        );
        item.setCategory(request.getCategory());
        item.setDescription(request.getDescription());
        item.setDefaultDiscountPct(request.getDefaultDiscountPct() != null ? request.getDefaultDiscountPct() : BigDecimal.ZERO);
        item.setTaxRatePct(request.getTaxRatePct() != null ? request.getTaxRatePct() : BigDecimal.ZERO);
        item.setHasTieredPricing(Boolean.TRUE.equals(request.getHasTieredPricing()));
        item.recalculateNetPrice();

        CatalogueItem saved = catalogueItemRepository.save(item);

        // Record initial change log
        ItemChangeLog initLog = new ItemChangeLog(
                saved,
                adminUser,
                "item_created",
                "ALL",
                null,
                "BasePrice: " + saved.getBasePrice() + ", Discount: " + saved.getDefaultDiscountPct() + "%",
                "manual",
                request.getNotes() != null ? request.getNotes() : "Initial SKU creation in catalogue"
        );
        changeLogRepository.save(initLog);

        // Save tiers if provided
        if (request.getPriceTiers() != null && !request.getPriceTiers().isEmpty()) {
            for (ItemPriceTierDto tierDto : request.getPriceTiers()) {
                ItemPriceTier tier = new ItemPriceTier(
                        saved,
                        tierDto.getMinQty(),
                        tierDto.getMaxQty(),
                        tierDto.getDiscountPct(),
                        tierDto.getLabel()
                );
                tierRepository.save(tier);
            }
            saved.setHasTieredPricing(true);
            catalogueItemRepository.save(saved);
        }

        return getItemById(saved.getId(), currentUser);
    }

    public CatalogueItemResponseDto updateItem(String id, CatalogueItemRequestDto request, UserPrincipal currentUser) {
        CatalogueItem item = catalogueItemRepository.findById(id)
                .filter(i -> !Boolean.TRUE.equals(i.getDeleted()))
                .orElseThrow(() -> new IllegalArgumentException("Catalogue item not found: " + id));

        User adminUser = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new IllegalStateException("Admin user not found: " + currentUser.getId()));

        List<ItemChangeLog> audits = new ArrayList<>();

        // Audit Base Price
        if (request.getBasePrice() != null && request.getBasePrice().compareTo(item.getBasePrice()) != 0) {
            audits.add(new ItemChangeLog(
                    item, adminUser, "field_update", "base_price",
                    item.getBasePrice().toString(), request.getBasePrice().toString(),
                    "manual", request.getNotes()
            ));
            item.setBasePrice(request.getBasePrice());
        }

        // Audit Default Discount
        if (request.getDefaultDiscountPct() != null && request.getDefaultDiscountPct().compareTo(item.getDefaultDiscountPct()) != 0) {
            audits.add(new ItemChangeLog(
                    item, adminUser, "field_update", "default_discount_pct",
                    item.getDefaultDiscountPct().toString(), request.getDefaultDiscountPct().toString(),
                    "manual", request.getNotes()
            ));
            item.setDefaultDiscountPct(request.getDefaultDiscountPct());
        }

        // Audit Item Name
        if (request.getItemName() != null && !request.getItemName().trim().equalsIgnoreCase(item.getItemName())) {
            audits.add(new ItemChangeLog(
                    item, adminUser, "field_update", "item_name",
                    item.getItemName(), request.getItemName().trim(),
                    "manual", request.getNotes()
            ));
            item.setItemName(request.getItemName().trim());
        }

        // Audit UOM
        if (request.getUom() != null && !request.getUom().trim().equalsIgnoreCase(item.getUom())) {
            audits.add(new ItemChangeLog(
                    item, adminUser, "field_update", "uom",
                    item.getUom(), request.getUom().trim(),
                    "manual", request.getNotes()
            ));
            item.setUom(request.getUom().trim());
        }

        if (request.getCategory() != null) item.setCategory(request.getCategory());
        if (request.getDescription() != null) item.setDescription(request.getDescription());
        if (request.getTaxRatePct() != null) item.setTaxRatePct(request.getTaxRatePct());
        if (request.getHasTieredPricing() != null) item.setHasTieredPricing(request.getHasTieredPricing());

        item.setLastUpdatedBy(adminUser);
        item.recalculateNetPrice();

        catalogueItemRepository.save(item);
        if (!audits.isEmpty()) {
            changeLogRepository.saveAll(audits);
        }

        return getItemById(item.getId(), currentUser);
    }

    public void deactivateItem(String id, UserPrincipal currentUser) {
        CatalogueItem item = catalogueItemRepository.findById(id)
                .filter(i -> !Boolean.TRUE.equals(i.getDeleted()))
                .orElseThrow(() -> new IllegalArgumentException("Catalogue item not found: " + id));

        User adminUser = userRepository.findById(currentUser.getId()).orElse(null);

        item.setActive(false);
        catalogueItemRepository.save(item);

        ItemChangeLog deactLog = new ItemChangeLog(
                item, adminUser, "item_deactivated", "is_active",
                "1", "0", "manual", "Item deactivated by admin"
        );
        changeLogRepository.save(deactLog);
    }

    @Transactional(readOnly = true)
    public PriceQuoteResponseDto calculatePriceQuote(String id, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }

        CatalogueItem item = catalogueItemRepository.findById(id)
                .filter(i -> !Boolean.TRUE.equals(i.getDeleted()))
                .orElseThrow(() -> new IllegalArgumentException("Catalogue item not found: " + id));

        BigDecimal applicableDiscount = item.getDefaultDiscountPct() != null ? item.getDefaultDiscountPct() : BigDecimal.ZERO;
        String tierLabel = "Standard Default Discount (" + applicableDiscount + "%)";

        // Check if tiered pricing applies
        List<ItemPriceTier> tiers = tierRepository.findByCatalogueItemIdOrderByMinQtyAsc(item.getId());
        if (!tiers.isEmpty()) {
            for (ItemPriceTier t : tiers) {
                boolean minSatisfied = quantity >= t.getMinQty();
                boolean maxSatisfied = t.getMaxQty() == null || quantity <= t.getMaxQty();
                if (minSatisfied && maxSatisfied) {
                    applicableDiscount = t.getDiscountPct();
                    tierLabel = t.getLabel() != null ? t.getLabel() : "Quantity Tier (" + t.getMinQty() + "+ units)";
                    break;
                }
            }
        }

        BigDecimal discountFactor = BigDecimal.ONE.subtract(applicableDiscount.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP));
        BigDecimal netUnitPrice = item.getBasePrice().multiply(discountFactor).setScale(2, RoundingMode.HALF_UP);
        BigDecimal subtotal = netUnitPrice.multiply(BigDecimal.valueOf(quantity)).setScale(2, RoundingMode.HALF_UP);
        BigDecimal taxRate = item.getTaxRatePct() != null ? item.getTaxRatePct() : BigDecimal.ZERO;
        BigDecimal taxAmount = subtotal.multiply(taxRate.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP)).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalAmount = subtotal.add(taxAmount).setScale(2, RoundingMode.HALF_UP);

        PriceQuoteResponseDto quote = new PriceQuoteResponseDto();
        quote.setItemId(item.getId());
        quote.setItemCode(item.getItemCode());
        quote.setItemName(item.getItemName());
        quote.setUom(item.getUom());
        quote.setQuantity(quantity);
        quote.setBaseUnitPrice(item.getBasePrice());
        quote.setApplicableDiscountPct(applicableDiscount);
        quote.setTierLabel(tierLabel);
        quote.setNetUnitPrice(netUnitPrice);
        quote.setSubtotal(subtotal);
        quote.setTaxRatePct(taxRate);
        quote.setTaxAmount(taxAmount);
        quote.setTotalAmount(totalAmount);

        return quote;
    }

    public ItemPriceTierDto addPriceTier(String itemId, ItemPriceTierDto tierDto, UserPrincipal currentUser) {
        CatalogueItem item = catalogueItemRepository.findById(itemId)
                .filter(i -> !Boolean.TRUE.equals(i.getDeleted()))
                .orElseThrow(() -> new IllegalArgumentException("Catalogue item not found: " + itemId));

        User adminUser = userRepository.findById(currentUser.getId()).orElse(null);

        ItemPriceTier tier = new ItemPriceTier(
                item,
                tierDto.getMinQty(),
                tierDto.getMaxQty(),
                tierDto.getDiscountPct(),
                tierDto.getLabel()
        );
        ItemPriceTier saved = tierRepository.save(tier);

        item.setHasTieredPricing(true);
        catalogueItemRepository.save(item);

        ItemChangeLog tierLog = new ItemChangeLog(
                item, adminUser, "field_update", "item_price_tiers",
                null, "Tier: min=" + tier.getMinQty() + ", max=" + tier.getMaxQty() + ", disc=" + tier.getDiscountPct() + "%",
                "manual", "Added pricing tier"
        );
        changeLogRepository.save(tierLog);

        return ItemPriceTierDto.fromEntity(saved);
    }

    public void removePriceTier(String tierId, UserPrincipal currentUser) {
        ItemPriceTier tier = tierRepository.findById(tierId)
                .orElseThrow(() -> new IllegalArgumentException("Price tier not found: " + tierId));

        CatalogueItem item = tier.getCatalogueItem();
        tierRepository.delete(tier);

        if (item != null) {
            List<ItemPriceTier> remaining = tierRepository.findByCatalogueItemIdOrderByMinQtyAsc(item.getId());
            if (remaining.isEmpty()) {
                item.setHasTieredPricing(false);
                catalogueItemRepository.save(item);
            }
        }
    }

    public ItemAttachmentResponseDto addAttachment(String itemId, ItemAttachmentRequestDto dto, UserPrincipal currentUser) {
        CatalogueItem item = catalogueItemRepository.findById(itemId)
                .filter(i -> !Boolean.TRUE.equals(i.getDeleted()))
                .orElseThrow(() -> new IllegalArgumentException("Catalogue item not found: " + itemId));

        User adminUser = userRepository.findById(currentUser.getId()).orElse(null);

        ItemAttachment attachment = new ItemAttachment();
        attachment.setCatalogueItem(item);
        attachment.setUploadedBy(adminUser);
        attachment.setAttachmentType(dto.getAttachmentType());
        attachment.setFileName(dto.getFileName());
        attachment.setStorageKey(dto.getStorageKey());
        attachment.setPublicUrl(dto.getPublicUrl());
        attachment.setMimeType(dto.getMimeType());
        attachment.setFileSizeBytes(dto.getFileSizeBytes());
        attachment.setDurationSeconds(dto.getDurationSeconds());
        attachment.setUpdateMethod(dto.getUpdateMethod() != null ? dto.getUpdateMethod() : "manual");

        ItemAttachment saved = attachmentRepository.save(attachment);

        String changeType = "voice_note".equalsIgnoreCase(dto.getAttachmentType()) ? "voice_note_added" : "attachment_added";
        ItemChangeLog log = new ItemChangeLog(
                item, adminUser, changeType, dto.getAttachmentType(),
                null, dto.getFileName(),
                dto.getUpdateMethod(), dto.getNotes()
        );
        log.setAttachmentId(saved.getId());
        changeLogRepository.save(log);

        return ItemAttachmentResponseDto.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public List<ItemChangeLogResponseDto> getChangeLog(String itemId, UserPrincipal currentUser) {
        return changeLogRepository.findByCatalogueItemIdOrderByCreatedAtDesc(itemId).stream()
                .map(ItemChangeLogResponseDto::fromEntity)
                .collect(Collectors.toList());
    }
}
