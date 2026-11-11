package com.salesnotes.dto;

import java.math.BigDecimal;

public class PriceQuoteResponseDto {

    private String itemId;
    private String itemCode;
    private String itemName;
    private String uom;
    private Integer quantity;
    private BigDecimal baseUnitPrice;
    private BigDecimal applicableDiscountPct;
    private String tierLabel;
    private BigDecimal netUnitPrice;
    private BigDecimal subtotal;
    private BigDecimal taxRatePct;
    private BigDecimal taxAmount;
    private BigDecimal totalAmount;

    public PriceQuoteResponseDto() {
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
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

    public String getUom() {
        return uom;
    }

    public void setUom(String uom) {
        this.uom = uom;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getBaseUnitPrice() {
        return baseUnitPrice;
    }

    public void setBaseUnitPrice(BigDecimal baseUnitPrice) {
        this.baseUnitPrice = baseUnitPrice;
    }

    public BigDecimal getApplicableDiscountPct() {
        return applicableDiscountPct;
    }

    public void setApplicableDiscountPct(BigDecimal applicableDiscountPct) {
        this.applicableDiscountPct = applicableDiscountPct;
    }

    public String getTierLabel() {
        return tierLabel;
    }

    public void setTierLabel(String tierLabel) {
        this.tierLabel = tierLabel;
    }

    public BigDecimal getNetUnitPrice() {
        return netUnitPrice;
    }

    public void setNetUnitPrice(BigDecimal netUnitPrice) {
        this.netUnitPrice = netUnitPrice;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getTaxRatePct() {
        return taxRatePct;
    }

    public void setTaxRatePct(BigDecimal taxRatePct) {
        this.taxRatePct = taxRatePct;
    }

    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
}
