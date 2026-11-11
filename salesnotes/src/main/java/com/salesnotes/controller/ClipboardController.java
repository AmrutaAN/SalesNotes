package com.salesnotes.controller;

import com.salesnotes.dto.*;
import com.salesnotes.security.UserPrincipal;
import com.salesnotes.service.ClipboardService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/api/v1/clipboard", "/api/clipboard"})
public class ClipboardController {

    private final ClipboardService clipboardService;

    public ClipboardController(ClipboardService clipboardService) {
        this.clipboardService = clipboardService;
    }

    @GetMapping
    public ResponseEntity<ClipboardSessionResponseDto> getClipboard(
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        return ResponseEntity.ok(clipboardService.getClipboard(currentUser));
    }

    @PostMapping("/stage")
    public ResponseEntity<ClipboardSessionResponseDto> stageEntry(
            @RequestBody StageEntryRequestDto request,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        if (request.getLogEntryId() == null || request.getLogEntryId().trim().isEmpty()) {
            throw new IllegalArgumentException("logEntryId is required");
        }
        return ResponseEntity.ok(clipboardService.stageEntry(request.getLogEntryId(), currentUser));
    }

    @PostMapping("/stage/{logEntryId}")
    public ResponseEntity<ClipboardSessionResponseDto> stageEntryByPath(
            @PathVariable String logEntryId,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        return ResponseEntity.ok(clipboardService.stageEntry(logEntryId, currentUser));
    }

    @PostMapping("/bulk-stage")
    public ResponseEntity<ClipboardSessionResponseDto> bulkStage(
            @RequestBody StageEntryRequestDto request,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        List<String> ids = request.getLogEntryIds();
        if (ids == null && request.getLogEntryId() != null) {
            ids = List.of(request.getLogEntryId());
        }
        return ResponseEntity.ok(clipboardService.bulkStageEntries(ids, currentUser));
    }

    @DeleteMapping("/entries/{id}")
    public ResponseEntity<ClipboardSessionResponseDto> unstageEntry(
            @PathVariable String id,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        return ResponseEntity.ok(clipboardService.unstageEntry(id, currentUser));
    }

    @DeleteMapping("/entries/by-log-entry/{logEntryId}")
    public ResponseEntity<ClipboardSessionResponseDto> unstageByLogEntryId(
            @PathVariable String logEntryId,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        return ResponseEntity.ok(clipboardService.unstageByLogEntryId(logEntryId, currentUser));
    }

    @DeleteMapping
    public ResponseEntity<ClipboardSessionResponseDto> clearClipboard(
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        return ResponseEntity.ok(clipboardService.clearClipboard(currentUser));
    }

    @PostMapping("/export")
    public ResponseEntity<ExportResponseDto> exportClipboard(
            @Valid @RequestBody ExportRequestDto request,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clipboardService.exportClipboard(request, currentUser));
    }

    @GetMapping("/exports")
    public ResponseEntity<List<ExportResponseDto>> getExportHistory(
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        return ResponseEntity.ok(clipboardService.getExportHistory(currentUser));
    }

    @GetMapping("/exports/{id}")
    public ResponseEntity<ExportResponseDto> getExportById(
            @PathVariable String id,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        return ResponseEntity.ok(clipboardService.getExportById(id, currentUser));
    }
}
