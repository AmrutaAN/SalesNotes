package com.salesnotes.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class ClipboardEntryResponseDto {

    private String id;
    private LocalDateTime stagedAt;
    private String logEntryId;
    private String interactionType;
    private LocalDate interactionDate;
    private LocalTime interactionTime;
    private String textContent;
    private Boolean priority;
    private Boolean hasAttachments;
    private Boolean hasVoiceNote;
    private Boolean hasTranscription;
    private String clientNoteId;
    private String clientId;
    private String clientName;
    private String clientCompany;
    private String tagId;
    private String tagCode;
    private String tagName;
    private String tagColor;
    private String deepLinkUrl;

    public ClipboardEntryResponseDto() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDateTime getStagedAt() {
        return stagedAt;
    }

    public void setStagedAt(LocalDateTime stagedAt) {
        this.stagedAt = stagedAt;
    }

    public String getLogEntryId() {
        return logEntryId;
    }

    public void setLogEntryId(String logEntryId) {
        this.logEntryId = logEntryId;
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

    public String getClientNoteId() {
        return clientNoteId;
    }

    public void setClientNoteId(String clientNoteId) {
        this.clientNoteId = clientNoteId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientCompany() {
        return clientCompany;
    }

    public void setClientCompany(String clientCompany) {
        this.clientCompany = clientCompany;
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

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getTagColor() {
        return tagColor;
    }

    public void setTagColor(String tagColor) {
        this.tagColor = tagColor;
    }

    public String getDeepLinkUrl() {
        return deepLinkUrl;
    }

    public void setDeepLinkUrl(String deepLinkUrl) {
        this.deepLinkUrl = deepLinkUrl;
    }
}
