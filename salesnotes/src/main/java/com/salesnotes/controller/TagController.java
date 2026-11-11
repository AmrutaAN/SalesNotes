package com.salesnotes.controller;

import com.salesnotes.dto.TagRequestDto;
import com.salesnotes.dto.TagResponseDto;
import com.salesnotes.security.UserPrincipal;
import com.salesnotes.service.TagService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/api/v1/tags", "/api/tags"})
public class TagController {

    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping
    public ResponseEntity<List<TagResponseDto>> listTags(@AuthenticationPrincipal UserPrincipal currentUser) {
        return ResponseEntity.ok(tagService.listTags(currentUser));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SYSADMIN', 'COMPANYADMIN')")
    public ResponseEntity<TagResponseDto> createTag(
            @Valid @RequestBody TagRequestDto request,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tagService.createTag(request, currentUser));
    }
}
