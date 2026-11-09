package com.salesnotes.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "catalogue_items")
public class CatalogueItem extends BaseAuditEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organisation_id", nullable = false)
    private Organisation organisation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "last_updated_by", nullable = false)
    private User lastUpdatedBy;

    @NotBlank(message = "Item code (SKU) is required")
    @Column(name = "item_code", length = 80, nullable = false)
    private String itemCode;

    @NotBlank(message = "Item name is required")
    @Column(name = "item_name", length = 300, nullable = false)
    private String itemName;

    @Column(name = "category", length = 100)
    private String category;

    @NotBlank(message = "Unit of measure is required")
    @Column(name = "uom", length = 40, nullable = false)
    private String uom;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "Base price is required")
    @Column(name = "base_price", precision = 14, scale = 2, nullable = false)
    private BigDecimal basePrice;

    @Column(name = "default_discount_pct", precision = 5, scale = 2, nullable = false)
    private BigDecimal defaultDiscountPct = BigDecimal.ZERO;

    @Column(name = "net_price", precision = 14, scale = 2, nullable = false)
    private BigDecimal netPrice = BigDecimal.ZERO;

    @Column(name = "tax_rate_pct", precision = 5, scale = 2, nullable = false)
    private BigDecimal taxRatePct = BigDecimal.ZERO;

    @Column(name = "has_tiered_pricing", nullable = false)
    private Boolean hasTieredPricing = false;

    @Column(name = "is_active", nullable = false)
    private Boolean active = true;

    @OneToMany(mappedBy = "catalogueItem", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ItemPriceTier> priceTiers = new ArrayList<>();

    @OneToMany(mappedBy = "catalogueItem", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ItemAttachment> attachments = new ArrayList<>();

    @OneToMany(mappedBy = "catalogueItem", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("createdAt DESC")
    private List<ItemChangeLog> changeLogs = new ArrayList<>();

    public CatalogueItem() {
        super();
    }

    public CatalogueItem(Organisation organisation, User adminUser, String itemCode, String itemName, String uom, BigDecimal basePrice) {
        super();
        this.organisation = organisation;
        this.createdBy = adminUser;
        this.lastUpdatedBy = adminUser;
        this.itemCode = itemCode;
        this.itemName = itemName;
        this.uom = uom;
        this.basePrice = basePrice;
        this.defaultDiscountPct = BigDecimal.ZERO;
        this.taxRatePct = BigDecimal.ZERO;
        this.hasTieredPricing = false;
        this.active = true;
        recalculateNetPrice();
    }

    @PrePersist
    @PreUpdate
    public void calculateValues() {
        recalculateNetPrice();
    }

    public void recalculateNetPrice() {
        if (basePrice != null) {
            BigDecimal discount = defaultDiscountPct != null ? defaultDiscountPct : BigDecimal.ZERO;
            BigDecimal factor = BigDecimal.ONE.subtract(discount.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP));
            this.netPrice = basePrice.multiply(factor).setScale(2, RoundingMode.HALF_UP);
        } else {
            this.netPrice = BigDecimal.ZERO;
        }
    }

    public Organisation getOrganisation() {
        return organisation;
    }

    public void setOrganisation(Organisation organisation) {
        this.organisation = organisation;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public User getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy(User lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
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
        recalculateNetPrice();
    }

    public BigDecimal getDefaultDiscountPct() {
        return defaultDiscountPct;
    }

    public void setDefaultDiscountPct(BigDecimal defaultDiscountPct) {
        this.defaultDiscountPct = defaultDiscountPct;
        recalculateNetPrice();
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

    public List<ItemPriceTier> getPriceTiers() {
        return priceTiers;
    }

    public void setPriceTiers(List<ItemPriceTier> priceTiers) {
        this.priceTiers = priceTiers;
    }

    public List<ItemAttachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<ItemAttachment> attachments) {
        this.attachments = attachments;
    }

    public List<ItemChangeLog> getChangeLogs() {
        return changeLogs;
    }

    public void setChangeLogs(List<ItemChangeLog> changeLogs) {
        this.changeLogs = changeLogs;
    }
}
