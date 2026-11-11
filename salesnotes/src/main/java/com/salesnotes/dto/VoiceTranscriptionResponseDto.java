package com.salesnotes.dto;

import com.salesnotes.entity.VoiceTranscription;
import java.time.LocalDateTime;

public class VoiceTranscriptionResponseDto {

    private String id;
    private String status; // pending, processing, awaiting_confirmation, confirmed, discarded
    private String rawTranscript;
    private String editedTranscript;
    private String finalTranscript;
    private String sourceLanguageHint;
    private String aiModelUsed;
    private String logEntryAttachmentId;
    private String logEntryId;
    private String itemAttachmentId;
    private String requestedById;
    private String requestedByName;
    private LocalDateTime processingStartedAt;
    private LocalDateTime processingCompletedAt;
    private LocalDateTime confirmedAt;
    private LocalDateTime discardedAt;
    private LocalDateTime createdAt;

    public VoiceTranscriptionResponseDto() {
    }

    public static VoiceTranscriptionResponseDto fromEntity(VoiceTranscription vt) {
        VoiceTranscriptionResponseDto dto = new VoiceTranscriptionResponseDto();
        dto.setId(vt.getId());
        dto.setStatus(vt.getStatus());
        dto.setRawTranscript(vt.getRawTranscript());
        dto.setEditedTranscript(vt.getEditedTranscript());
        dto.setFinalTranscript(vt.getFinalTranscript());
        dto.setSourceLanguageHint(vt.getSourceLanguageHint());
        dto.setAiModelUsed(vt.getAiModelUsed());

        if (vt.getLogEntryAttachment() != null) {
            dto.setLogEntryAttachmentId(vt.getLogEntryAttachment().getId());
            if (vt.getLogEntryAttachment().getLogEntry() != null) {
                dto.setLogEntryId(vt.getLogEntryAttachment().getLogEntry().getId());
            }
        }

        if (vt.getItemAttachment() != null) {
            dto.setItemAttachmentId(vt.getItemAttachment().getId());
        }

        if (vt.getRequestedBy() != null) {
            dto.setRequestedById(vt.getRequestedBy().getId());
            dto.setRequestedByName(vt.getRequestedBy().getFullname());
        }

        dto.setProcessingStartedAt(vt.getProcessingStartedAt());
        dto.setProcessingCompletedAt(vt.getProcessingCompletedAt());
        dto.setConfirmedAt(vt.getConfirmedAt());
        dto.setDiscardedAt(vt.getDiscardedAt());
        dto.setCreatedAt(vt.getCreatedAt());
        return dto;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getLogEntryAttachmentId() {
        return logEntryAttachmentId;
    }

    public void setLogEntryAttachmentId(String logEntryAttachmentId) {
        this.logEntryAttachmentId = logEntryAttachmentId;
    }

    public String getLogEntryId() {
        return logEntryId;
    }

    public void setLogEntryId(String logEntryId) {
        this.logEntryId = logEntryId;
    }

    public String getItemAttachmentId() {
        return itemAttachmentId;
    }

    public void setItemAttachmentId(String itemAttachmentId) {
        this.itemAttachmentId = itemAttachmentId;
    }

    public String getRequestedById() {
        return requestedById;
    }

    public void setRequestedById(String requestedById) {
        this.requestedById = requestedById;
    }

    public String getRequestedByName() {
        return requestedByName;
    }

    public void setRequestedByName(String requestedByName) {
        this.requestedByName = requestedByName;
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
