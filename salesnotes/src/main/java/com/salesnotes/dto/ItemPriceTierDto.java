package com.salesnotes.dto;

import com.salesnotes.entity.ItemPriceTier;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class ItemPriceTierDto {

    private String id;

    @NotNull(message = "Minimum quantity is required")
    private Integer minQty;

    private Integer maxQty;

    @NotNull(message = "Discount percentage is required")
    private BigDecimal discountPct;

    private String label;

    public ItemPriceTierDto() {
    }

    public ItemPriceTierDto(Integer minQty, Integer maxQty, BigDecimal discountPct, String label) {
        this.minQty = minQty;
        this.maxQty = maxQty;
        this.discountPct = discountPct;
        this.label = label;
    }

    public static ItemPriceTierDto fromEntity(ItemPriceTier tier) {
        ItemPriceTierDto dto = new ItemPriceTierDto();
        dto.setId(tier.getId());
        dto.setMinQty(tier.getMinQty());
        dto.setMaxQty(tier.getMaxQty());
        dto.setDiscountPct(tier.getDiscountPct());
        dto.setLabel(tier.getLabel());
        return dto;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
}
