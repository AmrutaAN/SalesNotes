package com.salesnotes.dto;

import com.salesnotes.entity.ItemChangeLog;
import java.time.LocalDateTime;

public class ItemChangeLogResponseDto {

    private String id;
    private String catalogueItemId;
    private String changedById;
    private String changedByName;
    private String changeType;
    private String fieldName;
    private String oldValue;
    private String newValue;
    private String attachmentId;
    private String updateMethod;
    private String notes;
    private LocalDateTime createdAt;

    public ItemChangeLogResponseDto() {
    }

    public static ItemChangeLogResponseDto fromEntity(ItemChangeLog log) {
        ItemChangeLogResponseDto dto = new ItemChangeLogResponseDto();
        dto.setId(log.getId());
        if (log.getCatalogueItem() != null) {
            dto.setCatalogueItemId(log.getCatalogueItem().getId());
        }
        if (log.getChangedBy() != null) {
            dto.setChangedById(log.getChangedBy().getId());
            dto.setChangedByName(log.getChangedBy().getFullname());
        }
        dto.setChangeType(log.getChangeType());
        dto.setFieldName(log.getFieldName());
        dto.setOldValue(log.getOldValue());
        dto.setNewValue(log.getNewValue());
        dto.setAttachmentId(log.getAttachmentId());
        dto.setUpdateMethod(log.getUpdateMethod());
        dto.setNotes(log.getNotes());
        dto.setCreatedAt(log.getCreatedAt());
        return dto;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCatalogueItemId() {
        return catalogueItemId;
    }

    public void setCatalogueItemId(String catalogueItemId) {
        this.catalogueItemId = catalogueItemId;
    }

    public String getChangedById() {
        return changedById;
    }

    public void setChangedById(String changedById) {
        this.changedById = changedById;
    }

    public String getChangedByName() {
        return changedByName;
    }

    public void setChangedByName(String changedByName) {
        this.changedByName = changedByName;
    }

    public String getChangeType() {
        return changeType;
    }

    public void setChangeType(String changeType) {
        this.changeType = changeType;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getOldValue() {
        return oldValue;
    }

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }

    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    public String getAttachmentId() {
        return attachmentId;
    }

    public void setAttachmentId(String attachmentId) {
        this.attachmentId = attachmentId;
    }

    public String getUpdateMethod() {
        return updateMethod;
    }

    public void setUpdateMethod(String updateMethod) {
        this.updateMethod = updateMethod;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
