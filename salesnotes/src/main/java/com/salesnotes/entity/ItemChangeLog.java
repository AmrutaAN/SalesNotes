package com.salesnotes.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "item_change_log")
public class ItemChangeLog {

    @Id
    @Column(name = "id", length = 36, nullable = false, updatable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "catalogue_item_id", nullable = false)
    private CatalogueItem catalogueItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "changed_by", nullable = false)
    private User changedBy;

    @NotBlank(message = "Change type is required")
    @Column(name = "change_type", length = 30, nullable = false)
    private String changeType; // field_update, attachment_added, voice_note_added, item_created, item_deactivated

    @Column(name = "field_name", length = 80)
    private String fieldName;

    @Column(name = "old_value", columnDefinition = "TEXT")
    private String oldValue;

    @Column(name = "new_value", columnDefinition = "TEXT")
    private String newValue;

    @Column(name = "attachment_id", length = 36)
    private String attachmentId;

    @NotBlank(message = "Update method is required")
    @Column(name = "update_method", length = 20, nullable = false)
    private String updateMethod; // manual, attachment, voice_note

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public ItemChangeLog() {
        this.id = UUID.randomUUID().toString();
    }

    public ItemChangeLog(CatalogueItem catalogueItem, User changedBy, String changeType, String fieldName, String oldValue, String newValue, String updateMethod, String notes) {
        this.id = UUID.randomUUID().toString();
        this.catalogueItem = catalogueItem;
        this.changedBy = changedBy;
        this.changeType = changeType;
        this.fieldName = fieldName;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.updateMethod = updateMethod;
        this.notes = notes;
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

    public User getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(User changedBy) {
        this.changedBy = changedBy;
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
