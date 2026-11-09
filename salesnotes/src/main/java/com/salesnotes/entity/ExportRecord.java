package com.salesnotes.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "exports")
public class ExportRecord {

    @Id
    @Column(name = "id", length = 36, nullable = false, updatable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clipboard_session_id", nullable = false)
    private ClipboardSession clipboardSession;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exported_by", nullable = false)
    private User exportedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organisation_id", nullable = false)
    private Organisation organisation;

    @NotBlank(message = "Target module is required")
    @Column(name = "target_module", length = 30, nullable = false)
    private String targetModule; // sales_order, complaint, other

    @Column(name = "external_ref_id", length = 200)
    private String externalRefId;

    @NotBlank(message = "Export payload snapshot is required")
    @Column(name = "export_payload", columnDefinition = "TEXT", nullable = false)
    private String exportPayload; // JSON string snapshot

    @NotNull(message = "Entry count is required")
    @Column(name = "entry_count", nullable = false)
    private Integer entryCount;

    @Column(name = "status", length = 20, nullable = false)
    private String status = "pending"; // pending, success, failed

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "exported_at", nullable = false, updatable = false)
    private LocalDateTime exportedAt;

    public ExportRecord() {
        this.id = UUID.randomUUID().toString();
    }

    public ExportRecord(ClipboardSession clipboardSession, User exportedBy, Organisation organisation, String targetModule, String exportPayload, Integer entryCount) {
        this.id = UUID.randomUUID().toString();
        this.clipboardSession = clipboardSession;
        this.exportedBy = exportedBy;
        this.organisation = organisation;
        this.targetModule = targetModule;
        this.exportPayload = exportPayload;
        this.entryCount = entryCount;
        this.status = "pending";
    }

    @PrePersist
    protected void onCreate() {
        if (this.id == null || this.id.trim().isEmpty()) {
            this.id = UUID.randomUUID().toString();
        }
        if (this.exportedAt == null) {
            this.exportedAt = LocalDateTime.now();
        }
        if (this.status == null) {
            this.status = "pending";
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ClipboardSession getClipboardSession() {
        return clipboardSession;
    }

    public void setClipboardSession(ClipboardSession clipboardSession) {
        this.clipboardSession = clipboardSession;
    }

    public User getExportedBy() {
        return exportedBy;
    }

    public void setExportedBy(User exportedBy) {
        this.exportedBy = exportedBy;
    }

    public Organisation getOrganisation() {
        return organisation;
    }

    public void setOrganisation(Organisation organisation) {
        this.organisation = organisation;
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

    public String getExportPayload() {
        return exportPayload;
    }

    public void setExportPayload(String exportPayload) {
        this.exportPayload = exportPayload;
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
}
