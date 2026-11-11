package com.salesnotes.dto;

import java.time.LocalDateTime;
import java.util.List;

public class ExportResponseDto {

    private String id;
    private String clipboardSessionId;
    private String exportedById;
    private String exportedByName;
    private String targetModule;
    private String externalRefId;
    private Integer entryCount;
    private String status;
    private String errorMessage;
    private LocalDateTime exportedAt;
    private String exportPayload;
    private List<ClipboardEntryResponseDto> exportedEntries;

    public ExportResponseDto() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClipboardSessionId() {
        return clipboardSessionId;
    }

    public void setClipboardSessionId(String clipboardSessionId) {
        this.clipboardSessionId = clipboardSessionId;
    }

    public String getExportedById() {
        return exportedById;
    }

    public void setExportedById(String exportedById) {
        this.exportedById = exportedById;
    }

    public String getExportedByName() {
        return exportedByName;
    }

    public void setExportedByName(String exportedByName) {
        this.exportedByName = exportedByName;
    }

    public String getTargetModule() {
        return targetModule;
    }

    public void setTargetModule(String targetModule) {
        this.targetModule = targetModule;
    }

    public String getExternalRefId() {
        return externalRefId;
    }

    public void setExternalRefId(String externalRefId) {
        this.externalRefId = externalRefId;
    }

    public Integer getEntryCount() {
        return entryCount;
    }

    public void setEntryCount(Integer entryCount) {
        this.entryCount = entryCount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public LocalDateTime getExportedAt() {
        return exportedAt;
    }

    public void setExportedAt(LocalDateTime exportedAt) {
        this.exportedAt = exportedAt;
    }

    public String getExportPayload() {
        return exportPayload;
    }

    public void setExportPayload(String exportPayload) {
        this.exportPayload = exportPayload;
    }

    public List<ClipboardEntryResponseDto> getExportedEntries() {
        return exportedEntries;
    }

    public void setExportedEntries(List<ClipboardEntryResponseDto> exportedEntries) {
        this.exportedEntries = exportedEntries;
    }
}
