package com.salesnotes.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.time.LocalTime;

public class LogEntryRequestDto {

    @NotBlank(message = "Interaction type is required")
    private String interactionType; // field_visit, phone_call, video_call, post_visit_note

    private LocalDate interactionDate;
    private LocalTime interactionTime;
    private String textContent;
    private String tagId;
    private Boolean priority = false;
    private String clientTempId;

    public LogEntryRequestDto() {
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

    public String getTagId() {
        return tagId;
    }

    public void setTagId(String tagId) {
        this.tagId = tagId;
    }

    public Boolean getPriority() {
        return priority;
    }

    public void setPriority(Boolean priority) {
        this.priority = priority;
    }

    public String getClientTempId() {
        return clientTempId;
    }

    public void setClientTempId(String clientTempId) {
        this.clientTempId = clientTempId;
    }
}
