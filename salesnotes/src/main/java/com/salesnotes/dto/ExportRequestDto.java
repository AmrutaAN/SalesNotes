package com.salesnotes.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

public class ExportRequestDto {

    @NotBlank(message = "Target module is required (e.g. sales_order, complaint, lead)")
    private String targetModule;

    private String externalRefId;

    private String notes;

    private List<String> entryIds; // optional: if null/empty, exports all staged entries

    private Boolean clearAfterExport = true; // defaults to true

    public ExportRequestDto() {
    }

    public ExportRequestDto(String targetModule) {
        this.targetModule = targetModule;
    }

    public ExportRequestDto(String targetModule, String externalRefId, List<String> entryIds) {
        this.targetModule = targetModule;
        this.externalRefId = externalRefId;
        this.entryIds = entryIds;
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

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public List<String> getEntryIds() {
        return entryIds;
    }

    public void setEntryIds(List<String> entryIds) {
        this.entryIds = entryIds;
    }

    public Boolean getClearAfterExport() {
        return clearAfterExport;
    }

    public void setClearAfterExport(Boolean clearAfterExport) {
        this.clearAfterExport = clearAfterExport;
    }
}
