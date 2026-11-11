package com.salesnotes.controller;

import com.salesnotes.dto.*;
import com.salesnotes.security.UserPrincipal;
import com.salesnotes.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping({"/api/v1/auth/login", "/api/auth/login"})
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping({"/api/v1/auth/refresh", "/api/auth/refresh"})
    public ResponseEntity<RefreshTokenResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refresh(request));
    }

    @GetMapping({"/api/v1/auth/me", "/api/auth/me"})
    public ResponseEntity<UserResponse> getCurrentUser(@AuthenticationPrincipal UserPrincipal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(authService.getCurrentUser(principal));
    }

    @PostMapping({"/api/v1/auth/reset-password/verify-old", "/api/auth/reset-password/verify-old"})
    public ResponseEntity<Map<String, Object>> resetPasswordUsingOld(@Valid @RequestBody VerifyOldPasswordRequest request) {
        return ResponseEntity.ok(authService.resetPasswordUsingOldPassword(request));
    }

    @PostMapping({"/api/v1/auth/forgot-password/request-link", "/api/auth/forgot-password/request-link"})
    public ResponseEntity<Map<String, Object>> requestPasswordResetLink(@Valid @RequestBody ForgotPasswordRequestDto request) {
        return ResponseEntity.ok(authService.requestPasswordResetLink(request));
    }

    @PostMapping({"/api/v1/auth/forgot-password/reset", "/api/auth/forgot-password/reset"})
    public ResponseEntity<Map<String, Object>> resetPasswordWithToken(@Valid @RequestBody ResetPasswordWithTokenDto request) {
        return ResponseEntity.ok(authService.resetPasswordWithToken(request));
    }

    @PostMapping({"/api/v1/auth/change-password", "/api/auth/change-password"})
    public ResponseEntity<Map<String, Object>> changePassword(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody ChangePasswordRequest request
    ) {
        return ResponseEntity.ok(authService.changePassword(principal, request));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, String>> handleBadCredentials(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Unauthorized", "message", ex.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Bad Request", "message", ex.getMessage()));
    }
}

