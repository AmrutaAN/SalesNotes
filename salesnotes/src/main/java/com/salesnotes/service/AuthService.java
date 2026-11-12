package com.salesnotes.service;

import com.salesnotes.dto.*;
import com.salesnotes.entity.User;
import com.salesnotes.repository.UserRepository;
import com.salesnotes.security.CustomUserDetailsService;
import com.salesnotes.security.JwtService;
import com.salesnotes.security.UserPrincipal;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Transactional
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    // In-memory token store for forgot-password reset requests (token -> ResetTokenEntry)
    private final Map<String, ResetTokenEntry> resetTokenStore = new ConcurrentHashMap<>();

    private static class ResetTokenEntry {
        final String username;
        final String email;
        final LocalDateTime expiresAt;

        ResetTokenEntry(String username, String email, LocalDateTime expiresAt) {
            this.username = username;
            this.email = email;
            this.expiresAt = expiresAt;
        }

        boolean isExpired() {
            return LocalDateTime.now().isAfter(expiresAt);
        }
    }

    public AuthService(
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            CustomUserDetailsService userDetailsService,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            EmailService emailService
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    public LoginResponse login(LoginRequest request) {
        String input = request.getUsernameOrEmail().trim();

        // 1. Locate user by username or email
        Optional<User> userOpt = userRepository.findByUsernameIgnoreCase(input)
                .or(() -> userRepository.findFirstByEmailIgnoreCase(input));

        if (userOpt.isEmpty()) {
            throw new BadCredentialsException("Username or email '" + input + "' not found.");
        }

        User user = userOpt.get();

        // 2. Validate password matches
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Wrong password entered for user '" + user.getUsername() + "'. Please verify your password or use 'Forgot Password'.");
        }

        // 3. Check active status
        if (Boolean.FALSE.equals(user.getActive())) {
            throw new BadCredentialsException("Account '" + user.getUsername() + "' has been deactivated. Please contact your administrator.");
        }

        // Authenticate credentials
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getUsername(),
                        request.getPassword()
                )
        );

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

        // Update last login timestamp
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        String accessToken = jwtService.generateAccessToken(principal);
        String refreshToken = jwtService.generateRefreshToken(principal);

        UserResponse userResponse = UserResponse.fromEntity(user);
        return new LoginResponse(
                accessToken,
                refreshToken,
                jwtService.getAccessTokenExpirationMs(),
                userResponse
        );
    }

    public Map<String, Object> resetPasswordUsingOldPassword(VerifyOldPasswordRequest request) {
        String input = request.getUsernameOrEmail().trim();
        User user = userRepository.findByUsernameIgnoreCase(input)
                .or(() -> userRepository.findFirstByEmailIgnoreCase(input))
                .orElseThrow(() -> new IllegalArgumentException("Account with username or email '" + input + "' not found."));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Incorrect current/old password. Please verify your old password.");
        }

        if (request.getNewPassword() == null || request.getNewPassword().trim().length() < 6) {
            throw new IllegalArgumentException("New password must be at least 6 characters long.");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword().trim()));
        userRepository.save(user);

        return Map.of(
                "success", true,
                "message", "Password reset successfully using old password verification. You can now log in with your new password."
        );
    }

    public Map<String, Object> requestPasswordResetLink(ForgotPasswordRequestDto request) {
        String input = request.getUsernameOrEmail().trim();
        User user = userRepository.findByUsernameIgnoreCase(input)
                .or(() -> userRepository.findFirstByEmailIgnoreCase(input))
                .orElseThrow(() -> new IllegalArgumentException("Account with username or email '" + input + "' not found."));

        // Retrieve user's email from table users.email
        String targetEmail = user.getEmail().trim();

        // Generate 32-character secure reset token valid for 30 minutes
        String token = UUID.randomUUID().toString().replace("-", "");
        resetTokenStore.put(token, new ResetTokenEntry(user.getUsername(), targetEmail, LocalDateTime.now().plusMinutes(30)));

        // Send reset link directly to user's email; strictly NOT returned to screen
        emailService.sendPasswordResetEmail(targetEmail, user.getUsername(), token);

        return Map.of(
                "success", true,
                "message", "A password reset link has been dispatched to your registered email address.",
                "maskedEmail", maskEmail(targetEmail),
                "emailSent", true
        );
    }

    // Helper accessible for tests to simulate reading token delivered via email
    public String getActiveResetTokenForUser(String username) {
        return resetTokenStore.entrySet().stream()
                .filter(e -> e.getValue().username.equalsIgnoreCase(username) && !e.getValue().isExpired())
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }

    private String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return "your registered email";
        }
        int atIdx = email.indexOf('@');
        String name = email.substring(0, atIdx);
        String domain = email.substring(atIdx);
        if (name.length() <= 2) {
            return name.charAt(0) + "***" + domain;
        }
        return name.charAt(0) + "***" + name.charAt(name.length() - 1) + domain;
    }

    public Map<String, Object> resetPasswordWithToken(ResetPasswordWithTokenDto request) {
        String token = request.getToken().trim();
        ResetTokenEntry entry = resetTokenStore.get(token);

        if (entry == null || entry.isExpired()) {
            throw new IllegalArgumentException("Password reset link/token is invalid or has expired. Please request a new link.");
        }

        String username = (entry.username != null && !entry.username.isBlank())
                ? entry.username
                : (request.getUsernameOrEmail() != null ? request.getUsernameOrEmail().trim() : "");

        User user = userRepository.findByUsernameIgnoreCase(username)
                .or(() -> userRepository.findFirstByEmailIgnoreCase(username))
                .orElseThrow(() -> new IllegalArgumentException("User for token not found."));

        if (request.getNewPassword() == null || request.getNewPassword().trim().length() < 6) {
            throw new IllegalArgumentException("New password must be at least 6 characters long.");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword().trim()));
        userRepository.save(user);

        // Invalidate used token
        resetTokenStore.remove(token);

        return Map.of(
                "success", true,
                "message", "Password has been successfully reset. You can now sign in with your new password."
        );
    }

    public Map<String, Object> changePassword(UserPrincipal principal, ChangePasswordRequest request) {
        if (principal == null) {
            throw new IllegalStateException("Authentication required to change password.");
        }

        User user = userRepository.findById(principal.getId())
                .orElseThrow(() -> new IllegalStateException("Current user not found."));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect.");
        }

        if (request.getNewPassword() == null || request.getNewPassword().trim().length() < 6) {
            throw new IllegalArgumentException("New password must be at least 6 characters long.");
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("New password and confirm password do not match.");
        }

        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new IllegalArgumentException("New password cannot be the same as the current password.");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword().trim()));
        userRepository.save(user);

        return Map.of(
                "success", true,
                "message", "Your password has been changed successfully."
        );
    }

    public RefreshTokenResponse refresh(RefreshTokenRequest request) {
        String token = request.getRefreshToken();
        if (jwtService.isTokenExpired(token)) {
            throw new BadCredentialsException("Refresh token is expired");
        }

        String username = jwtService.extractUsername(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        if (!jwtService.isTokenValid(token, userDetails)) {
            throw new BadCredentialsException("Invalid refresh token");
        }

        UserPrincipal principal = (UserPrincipal) userDetails;
        String newAccessToken = jwtService.generateAccessToken(principal);

        return new RefreshTokenResponse(newAccessToken, jwtService.getAccessTokenExpirationMs());
    }

    @Transactional(readOnly = true)
    public UserResponse getCurrentUser(UserPrincipal principal) {
        return userRepository.findById(principal.getId())
                .map(UserResponse::fromEntity)
                .orElseThrow(() -> new IllegalStateException("Current user not found"));
    }
}
