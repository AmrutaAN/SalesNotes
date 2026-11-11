package com.salesnotes.controller;

import com.salesnotes.dto.RoleRequestDto;
import com.salesnotes.dto.RoleResponseDto;
import com.salesnotes.security.UserPrincipal;
import com.salesnotes.service.RoleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/api/v1/roles", "/api/roles"})
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    public ResponseEntity<List<RoleResponseDto>> listRoles() {
        return ResponseEntity.ok(roleService.listRoles());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoleResponseDto> getRoleById(@PathVariable String id) {
        return ResponseEntity.ok(roleService.getRoleById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPERUSER', 'SYSADMIN', 'COMPANYADMIN')")
    public ResponseEntity<RoleResponseDto> createRole(
            @Valid @RequestBody RoleRequestDto request,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(roleService.createRole(request, currentUser));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERUSER', 'SYSADMIN', 'COMPANYADMIN')")
    public ResponseEntity<RoleResponseDto> updateRole(
            @PathVariable String id,
            @Valid @RequestBody RoleRequestDto request,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        return ResponseEntity.ok(roleService.updateRole(id, request, currentUser));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPERUSER', 'SYSADMIN')")
    public ResponseEntity<Void> deleteRole(
            @PathVariable String id,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        roleService.deleteRole(id, currentUser);
        return ResponseEntity.noContent().build();
    }
}
