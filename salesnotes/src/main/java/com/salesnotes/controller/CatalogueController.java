package com.salesnotes.controller;

import com.salesnotes.dto.*;
import com.salesnotes.security.UserPrincipal;
import com.salesnotes.service.CatalogueService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/api/v1/catalogue", "/api/catalogue"})
public class CatalogueController {

    private final CatalogueService catalogueService;

    public CatalogueController(CatalogueService catalogueService) {
        this.catalogueService = catalogueService;
    }

    @GetMapping
    public ResponseEntity<Page<CatalogueItemResponseDto>> listItems(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "itemCode,asc") String sort,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        String[] sortParts = sort.split(",");
        Sort.Direction direction = sortParts.length > 1 && "desc".equalsIgnoreCase(sortParts[1]) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortParts[0]));

        return ResponseEntity.ok(catalogueService.listItems(currentUser, category, search, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CatalogueItemResponseDto> getItemById(
            @PathVariable String id,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        return ResponseEntity.ok(catalogueService.getItemById(id, currentUser));
    }

    @GetMapping("/{id}/quote")
    public ResponseEntity<PriceQuoteResponseDto> getPriceQuote(
            @PathVariable String id,
            @RequestParam(defaultValue = "1") int quantity
    ) {
        return ResponseEntity.ok(catalogueService.calculatePriceQuote(id, quantity));
    }

    @GetMapping("/{id}/changelog")
    @PreAuthorize("hasAnyRole('SYSADMIN', 'COMPANYADMIN')")
    public ResponseEntity<List<ItemChangeLogResponseDto>> getChangeLog(
            @PathVariable String id,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        return ResponseEntity.ok(catalogueService.getChangeLog(id, currentUser));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SYSADMIN', 'COMPANYADMIN')")
    public ResponseEntity<CatalogueItemResponseDto> createItem(
            @Valid @RequestBody CatalogueItemRequestDto request,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(catalogueService.createItem(request, currentUser));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('SYSADMIN', 'COMPANYADMIN')")
    public ResponseEntity<CatalogueItemResponseDto> updateItem(
            @PathVariable String id,
            @RequestBody CatalogueItemRequestDto request,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        return ResponseEntity.ok(catalogueService.updateItem(id, request, currentUser));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SYSADMIN', 'COMPANYADMIN')")
    public ResponseEntity<Void> deactivateItem(
            @PathVariable String id,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        catalogueService.deactivateItem(id, currentUser);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/tiers")
    @PreAuthorize("hasAnyRole('SYSADMIN', 'COMPANYADMIN')")
    public ResponseEntity<ItemPriceTierDto> addPriceTier(
            @PathVariable String id,
            @Valid @RequestBody ItemPriceTierDto tierDto,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(catalogueService.addPriceTier(id, tierDto, currentUser));
    }

    @DeleteMapping("/tiers/{tierId}")
    @PreAuthorize("hasAnyRole('SYSADMIN', 'COMPANYADMIN')")
    public ResponseEntity<Void> removePriceTier(
            @PathVariable String tierId,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        catalogueService.removePriceTier(tierId, currentUser);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/attachments")
    @PreAuthorize("hasAnyRole('SYSADMIN', 'COMPANYADMIN')")
    public ResponseEntity<ItemAttachmentResponseDto> addAttachment(
            @PathVariable String id,
            @Valid @RequestBody ItemAttachmentRequestDto dto,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(catalogueService.addAttachment(id, dto, currentUser));
    }
}
