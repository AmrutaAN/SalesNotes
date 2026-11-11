package com.salesnotes.controller;

import com.salesnotes.dto.ClientRequestDto;
import com.salesnotes.dto.ClientResponseDto;
import com.salesnotes.dto.ClientSummaryDto;
import com.salesnotes.security.UserPrincipal;
import com.salesnotes.service.ClientService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping({"/api/v1/clients", "/api/clients"})
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping
    public ResponseEntity<Page<ClientResponseDto>> listClients(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean priority,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        String[] sortParts = sort.split(",");
        Sort.Direction direction = sortParts.length > 1 && "asc".equalsIgnoreCase(sortParts[1]) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortParts[0]));

        return ResponseEntity.ok(clientService.listClients(currentUser, search, priority, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientResponseDto> getClientById(
            @PathVariable String id,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        return ResponseEntity.ok(clientService.getClientById(id, currentUser));
    }

    @GetMapping("/{id}/summary")
    public ResponseEntity<ClientSummaryDto> getClientSummary(
            @PathVariable String id,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        return ResponseEntity.ok(clientService.getClientSummary(id, currentUser));
    }

    @PostMapping
    public ResponseEntity<ClientResponseDto> createClient(
            @Valid @RequestBody ClientRequestDto request,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clientService.createClient(request, currentUser));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ClientResponseDto> updateClient(
            @PathVariable String id,
            @Valid @RequestBody ClientRequestDto request,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        return ResponseEntity.ok(clientService.updateClient(id, request, currentUser));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(
            @PathVariable String id,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        clientService.deleteClient(id, currentUser);
        return ResponseEntity.noContent().build();
    }
}
