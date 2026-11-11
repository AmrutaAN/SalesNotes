package com.salesnotes.service;

import com.salesnotes.dto.UserRequest;
import com.salesnotes.dto.UserResponse;
import com.salesnotes.entity.User;
import com.salesnotes.entity.UserRole;
import com.salesnotes.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            org.springframework.security.crypto.password.PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UserResponse> getAllUsers(String search, UserRole role, Boolean active) {
        List<User> list;
        if (search != null && !search.trim().isEmpty()) {
            String term = search.trim();
            list = userRepository.findByFullnameContainingIgnoreCaseOrUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(term, term, term);
        } else if (role != null) {
            list = userRepository.findByRole(role);
        } else if (active != null) {
            list = userRepository.findByActive(active);
        } else {
            list = userRepository.findAll();
        }
        return list.stream().map(UserResponse::fromEntity).collect(Collectors.toList());
    }

    public Optional<UserResponse> getUserById(String id) {
        return userRepository.findById(id).map(UserResponse::fromEntity);
    }

    public Optional<UserResponse> getUserByUsername(String username) {
        return userRepository.findByUsernameIgnoreCase(username).map(UserResponse::fromEntity);
    }

    public UserResponse createUser(UserRequest request) {
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Password is required for creating a new user");
        }
        if (userRepository.existsByUsernameIgnoreCase(request.getUsername())) {
            throw new IllegalArgumentException("Username already in use: " + request.getUsername());
        }
        if (userRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new IllegalArgumentException("Email already in use: " + request.getEmail());
        }

        User user = new User(
                request.getUsername().trim(),
                request.getFullname().trim(),
                request.getEmail().trim(),
                passwordEncoder.encode(request.getPassword().trim()),
                request.getRole() != null ? request.getRole() : UserRole.COMPANYEMPLOYEE
        );
        if (request.getActive() != null) {
            user.setActive(request.getActive());
        }

        User saved = userRepository.save(user);
        return UserResponse.fromEntity(saved);
    }

    public Optional<UserResponse> updateUser(String id, UserRequest request) {
        return userRepository.findById(id).map(existing -> {
            if (!existing.getUsername().equalsIgnoreCase(request.getUsername()) &&
                    userRepository.existsByUsernameIgnoreCase(request.getUsername())) {
                throw new IllegalArgumentException("Username already taken: " + request.getUsername());
            }
            if (!existing.getEmail().equalsIgnoreCase(request.getEmail()) &&
                    userRepository.existsByEmailIgnoreCase(request.getEmail())) {
                throw new IllegalArgumentException("Email already taken: " + request.getEmail());
            }

            existing.setUsername(request.getUsername().trim());
            existing.setFullname(request.getFullname().trim());
            existing.setEmail(request.getEmail().trim());
            if (request.getPassword() != null && !request.getPassword().trim().isEmpty()) {
                existing.setPassword(passwordEncoder.encode(request.getPassword()));
            }
            if (request.getRole() != null) {
                existing.setRole(request.getRole());
            }
            if (request.getActive() != null) {
                existing.setActive(request.getActive());
            }
            return UserResponse.fromEntity(userRepository.save(existing));
        });
    }

    public Optional<UserResponse> assignRole(String id, String roleName) {
        return userRepository.findById(id).map(existing -> {
            UserRole newRole = UserRole.fromString(roleName);
            if (newRole == null) {
                throw new IllegalArgumentException("Invalid role specified: " + roleName);
            }
            existing.setRole(newRole);
            return UserResponse.fromEntity(userRepository.save(existing));
        });
    }

    public Optional<UserResponse> updateActiveStatus(String id, boolean active) {
        return userRepository.findById(id).map(existing -> {
            existing.setActive(active);
            return UserResponse.fromEntity(userRepository.save(existing));
        });
    }

    public boolean deleteUser(String id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
