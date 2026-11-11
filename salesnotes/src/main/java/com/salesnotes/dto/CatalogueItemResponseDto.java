package com.salesnotes.dto;

import com.salesnotes.entity.CatalogueItem;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CatalogueItemResponseDto {

    private String id;
    private String organisationId;
    private String itemCode;
    private String itemName;
    private String category;
    private String uom;
    private String description;
    private BigDecimal basePrice;
    private BigDecimal defaultDiscountPct;
    private BigDecimal netPrice;
    private BigDecimal taxRatePct;
    private Boolean hasTieredPricing;
    private Boolean active;
    private List<ItemPriceTierDto> priceTiers = new ArrayList<>();
    private List<ItemAttachmentResponseDto> attachments = new ArrayList<>();
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public CatalogueItemResponseDto() {
    }

    public static CatalogueItemResponseDto fromEntity(CatalogueItem item) {
        CatalogueItemResponseDto dto = new CatalogueItemResponseDto();
        dto.setId(item.getId());
        if (item.getOrganisation() != null) {
            dto.setOrganisationId(item.getOrganisation().getId());
        }
        dto.setItemCode(item.getItemCode());
        dto.setItemName(item.getItemName());
        dto.setCategory(item.getCategory());
        dto.setUom(item.getUom());
        dto.setDescription(item.getDescription());
        dto.setBasePrice(item.getBasePrice());
        dto.setDefaultDiscountPct(item.getDefaultDiscountPct());
        dto.setNetPrice(item.getNetPrice());
        dto.setTaxRatePct(item.getTaxRatePct());
        dto.setHasTieredPricing(item.getHasTieredPricing());
        dto.setActive(item.getActive());
        if (item.getPriceTiers() != null) {
            dto.setPriceTiers(item.getPriceTiers().stream()
                    .map(ItemPriceTierDto::fromEntity)
                    .collect(Collectors.toList()));
        }
        if (item.getAttachments() != null) {
            dto.setAttachments(item.getAttachments().stream()
                    .map(ItemAttachmentResponseDto::fromEntity)
                    .collect(Collectors.toList()));
        }
        dto.setCreatedAt(item.getCreatedAt());
        dto.setUpdatedAt(item.getUpdatedAt());
        return dto;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrganisationId() {
        return organisationId;
    }

    public void setOrganisationId(String organisationId) {
        this.organisationId = organisationId;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getUom() {
        return uom;
    }

    public void setUom(String uom) {
        this.uom = uom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(BigDecimal basePrice) {
        this.basePrice = basePrice;
    }

    public BigDecimal getDefaultDiscountPct() {
        return defaultDiscountPct;
    }

    public void setDefaultDiscountPct(BigDecimal defaultDiscountPct) {
        this.defaultDiscountPct = defaultDiscountPct;
    }

    public BigDecimal getNetPrice() {
        return netPrice;
    }

    public void setNetPrice(BigDecimal netPrice) {
        this.netPrice = netPrice;
    }

    public BigDecimal getTaxRatePct() {
        return taxRatePct;
    }

    public void setTaxRatePct(BigDecimal taxRatePct) {
        this.taxRatePct = taxRatePct;
    }

    public Boolean getHasTieredPricing() {
        return hasTieredPricing;
    }

    public void setHasTieredPricing(Boolean hasTieredPricing) {
        this.hasTieredPricing = hasTieredPricing;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public List<ItemPriceTierDto> getPriceTiers() {
        return priceTiers;
    }

    public void setPriceTiers(List<ItemPriceTierDto> priceTiers) {
        this.priceTiers = priceTiers;
    }

    public List<ItemAttachmentResponseDto> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<ItemAttachmentResponseDto> attachments) {
        this.attachments = attachments;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
