package com.salesnotes.dto;

import com.salesnotes.entity.Tag;
import jakarta.validation.constraints.NotBlank;

public class TagRequestDto {

    @NotBlank(message = "Tag code is required")
    private String code;

    @NotBlank(message = "Tag label is required")
    private String label;

    private String colorHex = "#1B4F8A";
    private String description;
    private Integer sortOrder = 0;

    public TagRequestDto() {
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getColorHex() {
        return colorHex;
    }

    public void setColorHex(String colorHex) {
        this.colorHex = colorHex;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }
}
