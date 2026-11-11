package com.salesnotes.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CatalogueItemRequestDto {

    @NotBlank(message = "Item code (SKU) is required")
    private String itemCode;

    @NotBlank(message = "Item name is required")
    private String itemName;

    private String category;

    @NotBlank(message = "Unit of measure is required")
    private String uom;

    private String description;

    @NotNull(message = "Base price is required")
    private BigDecimal basePrice;

    private BigDecimal defaultDiscountPct;
    private BigDecimal taxRatePct;
    private Boolean hasTieredPricing;
    private List<ItemPriceTierDto> priceTiers = new ArrayList<>();
    private String notes; // context for change log

    public CatalogueItemRequestDto() {
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

    public List<ItemPriceTierDto> getPriceTiers() {
        return priceTiers;
    }

    public void setPriceTiers(List<ItemPriceTierDto> priceTiers) {
        this.priceTiers = priceTiers;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
