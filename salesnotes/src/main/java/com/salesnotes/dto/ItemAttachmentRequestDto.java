package com.salesnotes.dto;

import com.salesnotes.entity.ItemAttachment;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

public class ItemAttachmentRequestDto {

    @NotBlank(message = "Attachment type is required")
    private String attachmentType; // document, image, voice_note

    @NotBlank(message = "File name is required")
    private String fileName;

    @NotBlank(message = "Storage key is required")
    private String storageKey;

    private String publicUrl;
    private String mimeType;
    private Long fileSizeBytes;
    private Integer durationSeconds;
    private String updateMethod = "manual"; // manual, attachment, voice_note
    private String notes;

    public ItemAttachmentRequestDto() {
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

    public String getStorageKey() {
        return storageKey;
    }

    public void setStorageKey(String storageKey) {
        this.storageKey = storageKey;
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

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
