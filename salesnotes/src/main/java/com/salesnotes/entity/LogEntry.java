package com.salesnotes.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "log_entries")
public class LogEntry extends BaseAuditEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_note_id", nullable = false)
    private ClientNote clientNote;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organisation_id", nullable = false)
    private Organisation organisation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id", nullable = false)
    private User createdByUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id")
    private Tag tag;

    @NotBlank(message = "Interaction type is required")
    @Column(name = "interaction_type", length = 25, nullable = false)
    private String interactionType; // field_visit, phone_call, video_call, post_visit_note

    @NotNull(message = "Interaction date is required")
    @Column(name = "interaction_date", nullable = false)
    private LocalDate interactionDate;

    @Column(name = "interaction_time")
    private LocalTime interactionTime;

    @Column(name = "text_content", columnDefinition = "TEXT")
    private String textContent;

    @Column(name = "is_priority", nullable = false)
    private Boolean priority = false;

    @Column(name = "has_attachments", nullable = false)
    private Boolean hasAttachments = false;

    @Column(name = "has_voice_note", nullable = false)
    private Boolean hasVoiceNote = false;

    @Column(name = "has_transcription", nullable = false)
    private Boolean hasTranscription = false;

    @Column(name = "client_temp_id", length = 36)
    private String clientTempId;

    @Column(name = "sync_version", nullable = false)
    private Long syncVersion = 1L;

    @OneToMany(mappedBy = "logEntry", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<LogEntryAttachment> attachments = new ArrayList<>();

    public LogEntry() {
        super();
    }

    public LogEntry(ClientNote clientNote, Organisation organisation, User createdByUser, String interactionType, LocalDate interactionDate, String textContent) {
        super();
        this.clientNote = clientNote;
        this.organisation = organisation;
        this.createdByUser = createdByUser;
        this.interactionType = interactionType;
        this.interactionDate = interactionDate;
        this.textContent = textContent;
        this.priority = false;
        this.hasAttachments = false;
        this.hasVoiceNote = false;
        this.hasTranscription = false;
        this.syncVersion = 1L;
    }

    public ClientNote getClientNote() {
        return clientNote;
    }

    public void setClientNote(ClientNote clientNote) {
        this.clientNote = clientNote;
    }

    public Organisation getOrganisation() {
        return organisation;
    }

    public void setOrganisation(Organisation organisation) {
        this.organisation = organisation;
    }

    public User getCreatedByUser() {
        return createdByUser;
    }

    public void setCreatedByUser(User createdByUser) {
        this.createdByUser = createdByUser;
    }

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
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

    public List<LogEntryAttachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<LogEntryAttachment> attachments) {
        this.attachments = attachments;
    }
}
