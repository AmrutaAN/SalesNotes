package com.salesnotes.dto;

import com.salesnotes.entity.LogEntryAttachment;
import java.time.LocalDateTime;

public class AttachmentResponseDto {

    private String id;
    private String attachmentType;
    private String fileName;
    private String publicUrl;
    private String linkUrl;
    private String linkTitle;
    private String mimeType;
    private Long fileSizeBytes;
    private Integer durationSeconds;
    private LocalDateTime createdAt;

    public AttachmentResponseDto() {
    }

    public static AttachmentResponseDto fromEntity(LogEntryAttachment a) {
        AttachmentResponseDto dto = new AttachmentResponseDto();
        dto.setId(a.getId());
        dto.setAttachmentType(a.getAttachmentType());
        dto.setFileName(a.getFileName());
        dto.setPublicUrl(a.getPublicUrl());
        dto.setLinkUrl(a.getLinkUrl());
        dto.setLinkTitle(a.getLinkTitle());
        dto.setMimeType(a.getMimeType());
        dto.setFileSizeBytes(a.getFileSizeBytes());
        dto.setDurationSeconds(a.getDurationSeconds());
        dto.setCreatedAt(a.getCreatedAt());
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

    public String getLinkUrl() {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }

    public String getLinkTitle() {
        return linkTitle;
    }

    public void setLinkTitle(String linkTitle) {
        this.linkTitle = linkTitle;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
