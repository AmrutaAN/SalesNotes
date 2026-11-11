package com.salesnotes.dto;

import com.salesnotes.entity.Tag;

public class TagResponseDto {

    private String id;
    private String code;
    private String label;
    private String colorHex;
    private String description;
    private Boolean isSystem;
    private Boolean isActive;
    private Integer sortOrder;

    public TagResponseDto() {
    }

    public static TagResponseDto fromEntity(Tag tag) {
        TagResponseDto dto = new TagResponseDto();
        dto.setId(tag.getId());
        dto.setCode(tag.getCode());
        dto.setLabel(tag.getLabel());
        dto.setColorHex(tag.getColorHex());
        dto.setDescription(tag.getDescription());
        dto.setIsSystem(tag.getIsSystem());
        dto.setIsActive(tag.getIsActive());
        dto.setSortOrder(tag.getSortOrder());
        return dto;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public Boolean getIsSystem() {
        return isSystem;
    }

    public void setIsSystem(Boolean isSystem) {
        this.isSystem = isSystem;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }
}
