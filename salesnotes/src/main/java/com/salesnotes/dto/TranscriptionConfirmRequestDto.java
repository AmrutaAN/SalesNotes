package com.salesnotes.dto;

import jakarta.validation.constraints.NotBlank;

public class TranscriptionConfirmRequestDto {

    @NotBlank(message = "Final transcript is required")
    private String finalTranscript;

    private Boolean applyToNote = true; // default true: appends or sets into logEntry.textContent

    public TranscriptionConfirmRequestDto() {
    }

    public TranscriptionConfirmRequestDto(String finalTranscript) {
        this.finalTranscript = finalTranscript;
        this.applyToNote = true;
    }

    public TranscriptionConfirmRequestDto(String finalTranscript, Boolean applyToNote) {
        this.finalTranscript = finalTranscript;
        this.applyToNote = applyToNote;
    }

    public String getFinalTranscript() {
        return finalTranscript;
    }

    public void setFinalTranscript(String finalTranscript) {
        this.finalTranscript = finalTranscript;
    }

    public Boolean getApplyToNote() {
        return applyToNote;
    }

    public void setApplyToNote(Boolean applyToNote) {
        this.applyToNote = applyToNote;
    }
}
