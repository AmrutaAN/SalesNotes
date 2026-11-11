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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;
    private final UserRepository userRepository;

    public AuthService(
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            CustomUserDetailsService userDetailsService,
            UserRepository userRepository
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.userRepository = userRepository;
    }

    public LoginResponse login(LoginRequest request) {
        // Authenticate credentials
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsernameOrEmail().trim(),
                        request.getPassword()
                )
        );

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

        // Update last login timestamp
        User user = userRepository.findById(principal.getId()).orElse(null);
        if (user != null) {
            user.setLastLoginAt(LocalDateTime.now());
            userRepository.save(user);
        }

        String accessToken = jwtService.generateAccessToken(principal);
        String refreshToken = jwtService.generateRefreshToken(principal);

        UserResponse userResponse = user != null ? UserResponse.fromEntity(user) : null;
        return new LoginResponse(
                accessToken,
                refreshToken,
                jwtService.getAccessTokenExpirationMs(),
                userResponse
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
