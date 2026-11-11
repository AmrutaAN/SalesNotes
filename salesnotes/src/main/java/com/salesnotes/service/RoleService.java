package com.salesnotes.service;

import com.salesnotes.dto.RoleRequestDto;
import com.salesnotes.dto.RoleResponseDto;
import com.salesnotes.entity.Role;
import com.salesnotes.entity.UserRole;
import com.salesnotes.repository.RoleRepository;
import com.salesnotes.repository.UserRepository;
import com.salesnotes.security.UserPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class RoleService {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    public RoleService(RoleRepository roleRepository, UserRepository userRepository) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<RoleResponseDto> listRoles() {
        return roleRepository.findAll().stream()
                .map(role -> {
                    long count = 0L;
                    try {
                        UserRole ur = UserRole.fromString(role.getCode());
                        count = userRepository.countByRole(ur);
                    } catch (Exception ignored) {
                    }
                    return RoleResponseDto.fromEntity(role, count);
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public RoleResponseDto getRoleById(String id) {
        Role role = roleRepository.findById(id)
                .or(() -> roleRepository.findByCodeIgnoreCase(id))
                .orElseThrow(() -> new IllegalArgumentException("Role not found: " + id));

        long count = 0L;
        try {
            UserRole ur = UserRole.fromString(role.getCode());
            count = userRepository.countByRole(ur);
        } catch (Exception ignored) {
        }
        return RoleResponseDto.fromEntity(role, count);
    }

    public RoleResponseDto createRole(RoleRequestDto request, UserPrincipal currentUser) {
        String cleanCode = request.getCode().trim();
        if (roleRepository.existsByCodeIgnoreCase(cleanCode)) {
            throw new IllegalArgumentException("Role code already exists: " + cleanCode);
        }

        Role role = new Role();
        role.setCode(cleanCode);
        role.setName(request.getName().trim());
        role.setDescription(request.getDescription());
        role.setIsSystem(false);

        Role saved = roleRepository.save(role);
        return RoleResponseDto.fromEntity(saved, 0L);
    }

    public RoleResponseDto updateRole(String id, RoleRequestDto request, UserPrincipal currentUser) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Role not found: " + id));

        // System roles cannot change code, but name and description can be customized
        if (!Boolean.TRUE.equals(role.getIsSystem())) {
            String newCode = request.getCode().trim();
            if (!role.getCode().equalsIgnoreCase(newCode) && roleRepository.existsByCodeIgnoreCase(newCode)) {
                throw new IllegalArgumentException("Role code already in use: " + newCode);
            }
            role.setCode(newCode);
        }

        role.setName(request.getName().trim());
        role.setDescription(request.getDescription());

        Role saved = roleRepository.save(role);
        long count = 0L;
        try {
            UserRole ur = UserRole.fromString(saved.getCode());
            count = userRepository.countByRole(ur);
        } catch (Exception ignored) {
        }
        return RoleResponseDto.fromEntity(saved, count);
    }

    public void deleteRole(String id, UserPrincipal currentUser) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Role not found: " + id));

        if (Boolean.TRUE.equals(role.getIsSystem())) {
            throw new IllegalArgumentException("System roles (" + role.getCode() + ") cannot be deleted.");
        }

        try {
            UserRole ur = UserRole.fromString(role.getCode());
            if (userRepository.countByRole(ur) > 0) {
                throw new IllegalStateException("Cannot delete role '" + role.getCode() + "' because users are currently assigned to it.");
            }
        } catch (Exception ignored) {
        }

        roleRepository.delete(role);
    }
}
