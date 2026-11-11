package com.salesnotes.controller;

import com.salesnotes.dto.*;
import com.salesnotes.security.UserPrincipal;
import com.salesnotes.service.AudioStorageService;
import com.salesnotes.service.VoiceTranscriptionService;
import jakarta.validation.Valid;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping
public class VoiceTranscriptionController {

    private final VoiceTranscriptionService voiceTranscriptionService;
    private final AudioStorageService audioStorageService;

    public VoiceTranscriptionController(
            VoiceTranscriptionService voiceTranscriptionService,
            AudioStorageService audioStorageService
    ) {
        this.voiceTranscriptionService = voiceTranscriptionService;
        this.audioStorageService = audioStorageService;
    }

    // 1. Audio Recording Attachment Upload
    @PostMapping(
            value = {"/api/v1/notes/{clientNoteId}/entries/{logEntryId}/audio", "/api/notes/{clientNoteId}/entries/{logEntryId}/audio"},
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<AttachmentResponseDto> uploadVoiceNote(
            @PathVariable String clientNoteId,
            @PathVariable String logEntryId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "durationSeconds", required = false) Integer durationSeconds,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(voiceTranscriptionService.uploadVoiceNote(logEntryId, file, durationSeconds, currentUser));
    }

    // 2. Stream Audio File
    @GetMapping({"/api/v1/audio/stream/{fileName}", "/api/audio/stream/{fileName}"})
    public ResponseEntity<Resource> streamAudioFile(@PathVariable String fileName) {
        Resource file = audioStorageService.loadAsResource(fileName);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getFilename() + "\"")
                .contentType(MediaType.parseMediaType("audio/mpeg"))
                .body(file);
    }

    // 3. Request Transcription
    @PostMapping({"/api/v1/transcriptions/request", "/api/transcriptions/request"})
    public ResponseEntity<VoiceTranscriptionResponseDto> requestTranscription(
            @Valid @RequestBody VoiceTranscriptionRequestDto request,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(voiceTranscriptionService.requestTranscription(request, currentUser));
    }

    // 4. Get Transcription by ID
    @GetMapping({"/api/v1/transcriptions/{id}", "/api/transcriptions/{id}"})
    public ResponseEntity<VoiceTranscriptionResponseDto> getTranscription(
            @PathVariable String id,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        return ResponseEntity.ok(voiceTranscriptionService.getTranscription(id, currentUser));
    }

    // 5. Get Transcription by Attachment ID
    @GetMapping({"/api/v1/transcriptions/by-attachment/{attachmentId}", "/api/transcriptions/by-attachment/{attachmentId}"})
    public ResponseEntity<VoiceTranscriptionResponseDto> getByAttachmentId(
            @PathVariable String attachmentId,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        return ResponseEntity.ok(voiceTranscriptionService.getByAttachmentId(attachmentId, currentUser));
    }

    // 6. Confirm Transcription (Two-step confirmation screen flow)
    @PostMapping({"/api/v1/transcriptions/{id}/confirm", "/api/transcriptions/{id}/confirm"})
    public ResponseEntity<VoiceTranscriptionResponseDto> confirmTranscription(
            @PathVariable String id,
            @Valid @RequestBody TranscriptionConfirmRequestDto request,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        return ResponseEntity.ok(voiceTranscriptionService.confirmTranscription(id, request, currentUser));
    }

    // 7. Discard Transcription
    @PostMapping({"/api/v1/transcriptions/{id}/discard", "/api/transcriptions/{id}/discard"})
    public ResponseEntity<VoiceTranscriptionResponseDto> discardTranscription(
            @PathVariable String id,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        return ResponseEntity.ok(voiceTranscriptionService.discardTranscription(id, currentUser));
    }

    // 8. List User Transcriptions
    @GetMapping({"/api/v1/transcriptions", "/api/transcriptions"})
    public ResponseEntity<List<VoiceTranscriptionResponseDto>> listUserTranscriptions(
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        return ResponseEntity.ok(voiceTranscriptionService.listUserTranscriptions(currentUser));
    }
}
