package com.salesnotes.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "voice_transcriptions")
public class VoiceTranscription {

    @Id
    @Column(name = "id", length = 36, nullable = false, updatable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requested_by", nullable = false)
    private User requestedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "log_entry_attachment_id")
    private LogEntryAttachment logEntryAttachment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_attachment_id")
    private ItemAttachment itemAttachment;

    @Column(name = "status", length = 30, nullable = false)
    private String status = "pending"; // pending, processing, awaiting_confirmation, confirmed, discarded

    @Column(name = "raw_transcript", columnDefinition = "TEXT")
    private String rawTranscript;

    @Column(name = "edited_transcript", columnDefinition = "TEXT")
    private String editedTranscript;

    @Column(name = "final_transcript", columnDefinition = "TEXT")
    private String finalTranscript;

    @Column(name = "source_language_hint", length = 20)
    private String sourceLanguageHint;

    @Column(name = "ai_model_used", length = 100)
    private String aiModelUsed;

    @Column(name = "processing_started_at")
    private LocalDateTime processingStartedAt;

    @Column(name = "processing_completed_at")
    private LocalDateTime processingCompletedAt;

    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    @Column(name = "discarded_at")
    private LocalDateTime discardedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public VoiceTranscription() {
        this.id = UUID.randomUUID().toString();
    }

    @PrePersist
    protected void onCreate() {
        if (this.id == null || this.id.trim().isEmpty()) {
            this.id = UUID.randomUUID().toString();
        }
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
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

    public User getRequestedBy() {
        return requestedBy;
    }

    public void setRequestedBy(User requestedBy) {
        this.requestedBy = requestedBy;
    }

    public LogEntryAttachment getLogEntryAttachment() {
        return logEntryAttachment;
    }

    public void setLogEntryAttachment(LogEntryAttachment logEntryAttachment) {
        this.logEntryAttachment = logEntryAttachment;
    }

    public ItemAttachment getItemAttachment() {
        return itemAttachment;
    }

    public void setItemAttachment(ItemAttachment itemAttachment) {
        this.itemAttachment = itemAttachment;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRawTranscript() {
        return rawTranscript;
    }

    public void setRawTranscript(String rawTranscript) {
        this.rawTranscript = rawTranscript;
    }

    public String getEditedTranscript() {
        return editedTranscript;
    }

    public void setEditedTranscript(String editedTranscript) {
        this.editedTranscript = editedTranscript;
    }

    public String getFinalTranscript() {
        return finalTranscript;
    }

    public void setFinalTranscript(String finalTranscript) {
        this.finalTranscript = finalTranscript;
    }

    public String getSourceLanguageHint() {
        return sourceLanguageHint;
    }

    public void setSourceLanguageHint(String sourceLanguageHint) {
        this.sourceLanguageHint = sourceLanguageHint;
    }

    public String getAiModelUsed() {
        return aiModelUsed;
    }

    public void setAiModelUsed(String aiModelUsed) {
        this.aiModelUsed = aiModelUsed;
    }

    public LocalDateTime getProcessingStartedAt() {
        return processingStartedAt;
    }

    public void setProcessingStartedAt(LocalDateTime processingStartedAt) {
        this.processingStartedAt = processingStartedAt;
    }

    public LocalDateTime getProcessingCompletedAt() {
        return processingCompletedAt;
    }

    public void setProcessingCompletedAt(LocalDateTime processingCompletedAt) {
        this.processingCompletedAt = processingCompletedAt;
    }

    public LocalDateTime getConfirmedAt() {
        return confirmedAt;
    }

    public void setConfirmedAt(LocalDateTime confirmedAt) {
        this.confirmedAt = confirmedAt;
    }

    public LocalDateTime getDiscardedAt() {
        return discardedAt;
    }

    public void setDiscardedAt(LocalDateTime discardedAt) {
        this.discardedAt = discardedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
