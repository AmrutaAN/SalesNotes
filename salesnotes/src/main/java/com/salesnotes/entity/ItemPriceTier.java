package com.salesnotes.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "item_price_tiers")
public class ItemPriceTier {

    @Id
    @Column(name = "id", length = 36, nullable = false, updatable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "catalogue_item_id", nullable = false)
    private CatalogueItem catalogueItem;

    @NotNull(message = "Minimum quantity is required")
    @Column(name = "min_qty", nullable = false)
    private Integer minQty;

    @Column(name = "max_qty")
    private Integer maxQty;

    @NotNull(message = "Discount percentage is required")
    @Column(name = "discount_pct", precision = 5, scale = 2, nullable = false)
    private BigDecimal discountPct;

    @Column(name = "label", length = 100)
    private String label;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public ItemPriceTier() {
        this.id = UUID.randomUUID().toString();
    }

    public ItemPriceTier(CatalogueItem catalogueItem, Integer minQty, Integer maxQty, BigDecimal discountPct, String label) {
        this.id = UUID.randomUUID().toString();
        this.catalogueItem = catalogueItem;
        this.minQty = minQty;
        this.maxQty = maxQty;
        this.discountPct = discountPct;
        this.label = label;
    }

    @PrePersist
    protected void onCreate() {
        if (this.id == null || this.id.trim().isEmpty()) {
            this.id = UUID.randomUUID().toString();
        }
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public CatalogueItem getCatalogueItem() {
        return catalogueItem;
    }

    public void setCatalogueItem(CatalogueItem catalogueItem) {
        this.catalogueItem = catalogueItem;
    }

    public Integer getMinQty() {
        return minQty;
    }

    public void setMinQty(Integer minQty) {
        this.minQty = minQty;
    }

    public Integer getMaxQty() {
        return maxQty;
    }

    public void setMaxQty(Integer maxQty) {
        this.maxQty = maxQty;
    }

    public BigDecimal getDiscountPct() {
        return discountPct;
    }

    public void setDiscountPct(BigDecimal discountPct) {
        this.discountPct = discountPct;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
