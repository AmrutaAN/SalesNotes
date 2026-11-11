package com.salesnotes.dto;

import com.salesnotes.entity.ItemAttachment;
import java.time.LocalDateTime;

public class ItemAttachmentResponseDto {

    private String id;
    private String attachmentType;
    private String fileName;
    private String publicUrl;
    private String mimeType;
    private Long fileSizeBytes;
    private Integer durationSeconds;
    private String updateMethod;
    private LocalDateTime createdAt;

    public ItemAttachmentResponseDto() {
    }

    public static ItemAttachmentResponseDto fromEntity(ItemAttachment attachment) {
        ItemAttachmentResponseDto dto = new ItemAttachmentResponseDto();
        dto.setId(attachment.getId());
        dto.setAttachmentType(attachment.getAttachmentType());
        dto.setFileName(attachment.getFileName());
        dto.setPublicUrl(attachment.getPublicUrl());
        dto.setMimeType(attachment.getMimeType());
        dto.setFileSizeBytes(attachment.getFileSizeBytes());
        dto.setDurationSeconds(attachment.getDurationSeconds());
        dto.setUpdateMethod(attachment.getUpdateMethod());
        dto.setCreatedAt(attachment.getCreatedAt());
        return dto;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAttachmentType() {
        return attachmentType;
    }

    public void setAttachmentType(String attachmentType) {
        this.attachmentType = attachmentType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getPublicUrl() {
        return publicUrl;
    }

    public void setPublicUrl(String publicUrl) {
        this.publicUrl = publicUrl;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public Long getFileSizeBytes() {
        return fileSizeBytes;
    }

    public void setFileSizeBytes(Long fileSizeBytes) {
        this.fileSizeBytes = fileSizeBytes;
    }

    public Integer getDurationSeconds() {
        return durationSeconds;
    }

    public void setDurationSeconds(Integer durationSeconds) {
        this.durationSeconds = durationSeconds;
    }

    public String getUpdateMethod() {
        return updateMethod;
    }

    public void setUpdateMethod(String updateMethod) {
        this.updateMethod = updateMethod;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
