package com.salesnotes.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ClipboardSessionResponseDto {

    private String id;
    private String userId;
    private String status;
    private Integer entryCount;
    private LocalDateTime expiresAt;
    private LocalDateTime clearedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<ClipboardEntryResponseDto> entries = new ArrayList<>();

    public ClipboardSessionResponseDto() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getEntryCount() {
        return entryCount;
    }

    public void setEntryCount(Integer entryCount) {
        this.entryCount = entryCount;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public LocalDateTime getClearedAt() {
        return clearedAt;
    }

    public void setClearedAt(LocalDateTime clearedAt) {
        this.clearedAt = clearedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<ClipboardEntryResponseDto> getEntries() {
        return entries;
    }

    public void setEntries(List<ClipboardEntryResponseDto> entries) {
        this.entries = entries;
    }
}
