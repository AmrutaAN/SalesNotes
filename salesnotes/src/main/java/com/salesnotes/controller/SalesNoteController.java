package com.salesnotes.controller;

import com.salesnotes.dto.SalesNoteRequest;
import com.salesnotes.dto.SalesNoteResponse;
import com.salesnotes.entity.NoteStatus;
import com.salesnotes.entity.Priority;
import com.salesnotes.service.SalesNoteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notes")
public class SalesNoteController {

    private final SalesNoteService salesNoteService;

    public SalesNoteController(SalesNoteService salesNoteService) {
        this.salesNoteService = salesNoteService;
    }

    @GetMapping
    public ResponseEntity<List<SalesNoteResponse>> getAllNotes(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) NoteStatus status,
            @RequestParam(required = false) Priority priority,
            @RequestParam(required = false) Long customerId
    ) {
        return ResponseEntity.ok(salesNoteService.getNotes(search, status, priority, customerId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SalesNoteResponse> getNoteById(@PathVariable Long id) {
        return salesNoteService.getNoteById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<SalesNoteResponse> createNote(@Valid @RequestBody SalesNoteRequest request) {
        SalesNoteResponse created = salesNoteService.createNote(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SalesNoteResponse> updateNote(@PathVariable Long id, @Valid @RequestBody SalesNoteRequest request) {
        return salesNoteService.updateNote(id, request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<SalesNoteResponse> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String statusStr = body.get("status");
        if (statusStr == null) {
            return ResponseEntity.badRequest().build();
        }
        NoteStatus status = NoteStatus.valueOf(statusStr.toUpperCase());
        return salesNoteService.updateStatus(id, status)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNote(@PathVariable Long id) {
        if (salesNoteService.deleteNote(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
