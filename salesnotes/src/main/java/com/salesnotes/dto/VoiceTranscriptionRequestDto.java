package com.salesnotes.dto;

public class VoiceTranscriptionRequestDto {

    private String attachmentId;       // LogEntryAttachment ID
    private String itemAttachmentId;   // ItemAttachment ID
    private String languageHint;       // e.g. "en", "es", "hi"

    public VoiceTranscriptionRequestDto() {
    }

    public VoiceTranscriptionRequestDto(String attachmentId) {
        this.attachmentId = attachmentId;
    }

    public VoiceTranscriptionRequestDto(String attachmentId, String languageHint) {
        this.attachmentId = attachmentId;
        this.languageHint = languageHint;
    }

    public String getAttachmentId() {
        return attachmentId;
    }

    public void setAttachmentId(String attachmentId) {
        this.attachmentId = attachmentId;
    }

    public String getItemAttachmentId() {
        return itemAttachmentId;
    }

    public void setItemAttachmentId(String itemAttachmentId) {
        this.itemAttachmentId = itemAttachmentId;
    }

    public String getLanguageHint() {
        return languageHint;
    }

    public void setLanguageHint(String languageHint) {
        this.languageHint = languageHint;
    }
}
