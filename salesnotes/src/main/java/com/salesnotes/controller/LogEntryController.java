package com.salesnotes.controller;

import com.salesnotes.dto.*;
import com.salesnotes.security.UserPrincipal;
import com.salesnotes.service.LogEntryService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping
public class LogEntryController {

    private final LogEntryService logEntryService;

    public LogEntryController(LogEntryService logEntryService) {
        this.logEntryService = logEntryService;
    }

    @GetMapping({"/api/v1/notes/{clientNoteId}/entries", "/api/notes/{clientNoteId}/entries"})
    public ResponseEntity<Page<LogEntryResponseDto>> listEntries(
            @PathVariable String clientNoteId,
            @RequestParam(required = false) String tagId,
            @RequestParam(required = false) Boolean priority,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(logEntryService.listEntries(clientNoteId, tagId, priority, search, pageable, currentUser));
    }

    @PostMapping({"/api/v1/notes/{clientNoteId}/entries", "/api/notes/{clientNoteId}/entries"})
    public ResponseEntity<LogEntryResponseDto> createEntry(
            @PathVariable String clientNoteId,
            @Valid @RequestBody LogEntryRequestDto request,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(logEntryService.createEntry(clientNoteId, request, currentUser));
    }

    @GetMapping({"/api/v1/entries/{id}", "/api/entries/{id}"})
    public ResponseEntity<LogEntryResponseDto> getEntryById(
            @PathVariable String id,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        return ResponseEntity.ok(logEntryService.getEntryById(id, currentUser));
    }

    @PatchMapping({"/api/v1/entries/{id}", "/api/entries/{id}"})
    public ResponseEntity<LogEntryResponseDto> updateEntry(
            @PathVariable String id,
            @Valid @RequestBody LogEntryRequestDto request,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        return ResponseEntity.ok(logEntryService.updateEntry(id, request, currentUser));
    }

    @PatchMapping({"/api/v1/entries/{id}/tag", "/api/entries/{id}/tag"})
    public ResponseEntity<LogEntryResponseDto> updateTag(
            @PathVariable String id,
            @RequestBody TagUpdateDto dto,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        return ResponseEntity.ok(logEntryService.updateTag(id, dto.getTagId(), currentUser));
    }

    @PatchMapping({"/api/v1/entries/{id}/priority", "/api/entries/{id}/priority"})
    public ResponseEntity<LogEntryResponseDto> updatePriority(
            @PathVariable String id,
            @RequestBody PriorityUpdateDto dto,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        return ResponseEntity.ok(logEntryService.updatePriority(id, dto.getPriority(), currentUser));
    }

    @DeleteMapping({"/api/v1/entries/{id}", "/api/entries/{id}"})
    public ResponseEntity<Void> deleteEntry(
            @PathVariable String id,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        logEntryService.deleteEntry(id, currentUser);
        return ResponseEntity.noContent().build();
    }

    @PostMapping({"/api/v1/entries/{id}/attachments/link", "/api/entries/{id}/attachments/link"})
    public ResponseEntity<AttachmentResponseDto> addLinkAttachment(
            @PathVariable String id,
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        String url = body.get("url");
        String title = body.get("title");
        if (url == null || url.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(logEntryService.addLinkAttachment(id, url.trim(), title, currentUser));
    }

    @DeleteMapping({"/api/v1/attachments/{id}", "/api/attachments/{id}"})
    public ResponseEntity<Void> deleteAttachment(
            @PathVariable String id,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        logEntryService.deleteAttachment(id, currentUser);
        return ResponseEntity.noContent().build();
    }
}
