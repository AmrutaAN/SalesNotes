package com.salesnotes.dto;

import com.salesnotes.entity.LogEntry;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LogEntryResponseDto {

    private String id;
    private String clientNoteId;
    private String createdById;
    private String createdByName;
    private String tagId;
    private String tagCode;
    private String tagLabel;
    private String tagColorHex;
    private String interactionType;
    private LocalDate interactionDate;
    private LocalTime interactionTime;
    private String textContent;
    private Boolean priority;
    private Boolean hasAttachments;
    private Boolean hasVoiceNote;
    private Boolean hasTranscription;
    private String clientTempId;
    private Long syncVersion;
    private List<AttachmentResponseDto> attachments = new ArrayList<>();
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public LogEntryResponseDto() {
    }

    public static LogEntryResponseDto fromEntity(LogEntry entry) {
        LogEntryResponseDto dto = new LogEntryResponseDto();
        dto.setId(entry.getId());
        if (entry.getClientNote() != null) {
            dto.setClientNoteId(entry.getClientNote().getId());
        }
        if (entry.getCreatedByUser() != null) {
            dto.setCreatedById(entry.getCreatedByUser().getId());
            dto.setCreatedByName(entry.getCreatedByUser().getFullname());
        }
        if (entry.getTag() != null) {
            dto.setTagId(entry.getTag().getId());
            dto.setTagCode(entry.getTag().getCode());
            dto.setTagLabel(entry.getTag().getLabel());
            dto.setTagColorHex(entry.getTag().getColorHex());
        }
        dto.setInteractionType(entry.getInteractionType());
        dto.setInteractionDate(entry.getInteractionDate());
        dto.setInteractionTime(entry.getInteractionTime());
        dto.setTextContent(entry.getTextContent());
        dto.setPriority(entry.getPriority());
        dto.setHasAttachments(entry.getHasAttachments());
        dto.setHasVoiceNote(entry.getHasVoiceNote());
        dto.setHasTranscription(entry.getHasTranscription());
        dto.setClientTempId(entry.getClientTempId());
        dto.setSyncVersion(entry.getSyncVersion());
        if (entry.getAttachments() != null) {
            dto.setAttachments(entry.getAttachments().stream()
                    .map(AttachmentResponseDto::fromEntity)
                    .collect(Collectors.toList()));
        }
        dto.setCreatedAt(entry.getCreatedAt());
        dto.setUpdatedAt(entry.getUpdatedAt());
        return dto;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClientNoteId() {
        return clientNoteId;
    }

    public void setClientNoteId(String clientNoteId) {
        this.clientNoteId = clientNoteId;
    }

    public String getCreatedById() {
        return createdById;
    }

    public void setCreatedById(String createdById) {
        this.createdById = createdById;
    }

    public String getCreatedByName() {
        return createdByName;
    }

    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }

    public String getTagId() {
        return tagId;
    }

    public void setTagId(String tagId) {
        this.tagId = tagId;
    }

    public String getTagCode() {
        return tagCode;
    }

    public void setTagCode(String tagCode) {
        this.tagCode = tagCode;
    }

    public String getTagLabel() {
        return tagLabel;
    }

    public void setTagLabel(String tagLabel) {
        this.tagLabel = tagLabel;
    }

    public String getTagColorHex() {
        return tagColorHex;
    }

    public void setTagColorHex(String tagColorHex) {
        this.tagColorHex = tagColorHex;
    }

    public String getInteractionType() {
        return interactionType;
    }

    public void setInteractionType(String interactionType) {
        this.interactionType = interactionType;
    }

    public LocalDate getInteractionDate() {
        return interactionDate;
    }

    public void setInteractionDate(LocalDate interactionDate) {
        this.interactionDate = interactionDate;
    }

    public LocalTime getInteractionTime() {
        return interactionTime;
    }

    public void setInteractionTime(LocalTime interactionTime) {
        this.interactionTime = interactionTime;
    }

    public String getTextContent() {
        return textContent;
    }

    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }

    public Boolean getPriority() {
        return priority;
    }

    public void setPriority(Boolean priority) {
        this.priority = priority;
    }

    public Boolean getHasAttachments() {
        return hasAttachments;
    }

    public void setHasAttachments(Boolean hasAttachments) {
        this.hasAttachments = hasAttachments;
    }

    public Boolean getHasVoiceNote() {
        return hasVoiceNote;
    }

    public void setHasVoiceNote(Boolean hasVoiceNote) {
        this.hasVoiceNote = hasVoiceNote;
    }

    public Boolean getHasTranscription() {
        return hasTranscription;
    }

    public void setHasTranscription(Boolean hasTranscription) {
        this.hasTranscription = hasTranscription;
    }

    public String getClientTempId() {
        return clientTempId;
    }

    public void setClientTempId(String clientTempId) {
        this.clientTempId = clientTempId;
    }

    public Long getSyncVersion() {
        return syncVersion;
    }

    public void setSyncVersion(Long syncVersion) {
        this.syncVersion = syncVersion;
    }

    public List<AttachmentResponseDto> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<AttachmentResponseDto> attachments) {
        this.attachments = attachments;
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
}
