package com.salesnotes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.salesnotes.dto.LoginRequest;
import com.salesnotes.dto.LoginResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("devlocal")
class AuthTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private com.salesnotes.service.AuthService authService;

    @Test
    @DisplayName("Should successfully login seeded companyadmin and return JWT access token")
    void shouldLoginCompanyAdmin() throws Exception {
        LoginRequest req = new LoginRequest("companyadmin", "Admin@123");

        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.user.username").value("companyadmin"))
                .andReturn();

        LoginResponse loginResponse = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                LoginResponse.class
        );

        assertThat(loginResponse.getAccessToken()).isNotNull();

        // Verify protected endpoint access with the token
        mockMvc.perform(get("/api/v1/auth/me")
                        .header("Authorization", "Bearer " + loginResponse.getAccessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("companyadmin"))
                .andExpect(jsonPath("$.role").value("companyadmin"));
    }

    @Test
    @DisplayName("Should reject login with invalid credentials and prompt wrong password")
    void shouldRejectInvalidCredentials() throws Exception {
        LoginRequest req = new LoginRequest("companyadmin", "WrongPass@999");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("Wrong password entered")));
    }

    @Test
    @DisplayName("Should prompt when username does not exist")
    void shouldPromptWhenUserNotFound() throws Exception {
        LoginRequest req = new LoginRequest("nonexistentuser", "SomePass@123");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("not found")));
    }

    @Test
    @DisplayName("Should reset password using old password verification")
    void shouldResetPasswordUsingOldPassword() throws Exception {
        com.salesnotes.dto.VerifyOldPasswordRequest req = new com.salesnotes.dto.VerifyOldPasswordRequest(
                "companyemployee", "User@123", "NewRepPass@123"
        );

        mockMvc.perform(post("/api/v1/auth/reset-password/verify-old")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        // Restore original password
        com.salesnotes.dto.VerifyOldPasswordRequest restoreReq = new com.salesnotes.dto.VerifyOldPasswordRequest(
                "companyemployee", "NewRepPass@123", "User@123"
        );
        mockMvc.perform(post("/api/v1/auth/reset-password/verify-old")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(restoreReq)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should request password reset link sent to mail (not on screen) and reset password using token")
    void shouldResetPasswordUsingToken() throws Exception {
        com.salesnotes.dto.ForgotPasswordRequestDto linkReq = new com.salesnotes.dto.ForgotPasswordRequestDto("companyemployee");

        mockMvc.perform(post("/api/v1/auth/forgot-password/request-link")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(linkReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.maskedEmail").isNotEmpty())
                .andExpect(jsonPath("$.emailSent").value(true))
                .andExpect(jsonPath("$.token").doesNotExist())
                .andExpect(jsonPath("$.resetUrl").doesNotExist());

        // Simulate user retrieving token from their email inbox
        String token = authService.getActiveResetTokenForUser("companyemployee");
        assertThat(token).isNotNull().hasSize(32);

        // Complete reset using token
        com.salesnotes.dto.ResetPasswordWithTokenDto resetReq = new com.salesnotes.dto.ResetPasswordWithTokenDto(
                token, "companyemployee", "ResetTokenPass@123"
        );

        mockMvc.perform(post("/api/v1/auth/forgot-password/reset")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(resetReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        // Restore password back to User@123
        com.salesnotes.dto.VerifyOldPasswordRequest restore = new com.salesnotes.dto.VerifyOldPasswordRequest(
                "companyemployee", "ResetTokenPass@123", "User@123"
        );
        mockMvc.perform(post("/api/v1/auth/reset-password/verify-old")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(restore)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should allow user to change password after login")
    void shouldChangePasswordAfterLogin() throws Exception {
        // 1. Login as sysadmin
        LoginRequest loginReq = new LoginRequest("sysadmin", "SysAdmin@123");
        MvcResult loginRes = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginReq)))
                .andExpect(status().isOk())
                .andReturn();

        String token = objectMapper.readTree(loginRes.getResponse().getContentAsString()).get("accessToken").asText();

        // 2. Change password
        com.salesnotes.dto.ChangePasswordRequest changeReq = new com.salesnotes.dto.ChangePasswordRequest(
                "SysAdmin@123", "NewSysPass@123", "NewSysPass@123"
        );

        mockMvc.perform(post("/api/v1/auth/change-password")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changeReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        // 3. Restore back to SysAdmin@123
        com.salesnotes.dto.ChangePasswordRequest restoreReq = new com.salesnotes.dto.ChangePasswordRequest(
                "NewSysPass@123", "SysAdmin@123", "SysAdmin@123"
        );

        mockMvc.perform(post("/api/v1/auth/change-password")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(restoreReq)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should deny access to unauthenticated requests on protected endpoints")
    void shouldDenyUnauthenticatedAccess() throws Exception {
        mockMvc.perform(get("/api/v1/auth/me"))
                .andExpect(status().isUnauthorized());
    }

    @Autowired
    private com.salesnotes.repository.UserRepository userRepository;

    @Autowired
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("HybridPasswordEncoder: if encrypted decrypt/verify, else verify as text pattern")
    void shouldVerifyEncryptedOrPlainTextPasswords() {
        com.salesnotes.security.HybridPasswordEncoder hybrid = (com.salesnotes.security.HybridPasswordEncoder) passwordEncoder;

        // 1. BCrypt encrypted
        String bcryptHash = hybrid.encode("Secret@123");
        assertThat(hybrid.isEncrypted(bcryptHash)).isTrue();
        assertThat(hybrid.matches("Secret@123", bcryptHash)).isTrue();
        assertThat(hybrid.matches("WrongPass@123", bcryptHash)).isFalse();

        // 2. Plain text pattern
        String plainText = "MyPlainPassword@2026";
        assertThat(hybrid.isEncrypted(plainText)).isFalse();
        assertThat(hybrid.matches("MyPlainPassword@2026", plainText)).isTrue();
        assertThat(hybrid.matches("WrongPass@2026", plainText)).isFalse();

        // 3. AES encrypted
        String aesEncrypted = hybrid.encryptAes("AesSecret@456");
        assertThat(hybrid.isEncrypted(aesEncrypted)).isTrue();
        assertThat(hybrid.matches("AesSecret@456", aesEncrypted)).isTrue();
        assertThat(hybrid.matches("WrongAes@456", aesEncrypted)).isFalse();
    }

    @Test
    @DisplayName("Should successfully authenticate a user whose password in database is plain text")
    void shouldAuthenticateUserWithPlainTextPassword() throws Exception {
        // Create a user directly with plain text unencrypted password
        String testUsername = "plainuser_" + System.currentTimeMillis();
        com.salesnotes.entity.Organisation org = userRepository.findByUsernameIgnoreCase("companyadmin")
                .map(com.salesnotes.entity.User::getOrganisation)
                .orElse(null);

        com.salesnotes.entity.User plainUser = new com.salesnotes.entity.User(
                testUsername,
                "Plain Text Password User",
                testUsername + "@salesnotes.test",
                "PlainText12345", // unencrypted plain text pattern
                com.salesnotes.entity.UserRole.COMPANYEMPLOYEE
        );
        plainUser.setOrganisation(org);
        userRepository.save(plainUser);

        try {
            // Attempt login with the plain text password
            LoginRequest req = new LoginRequest(testUsername, "PlainText12345");

            mockMvc.perform(post("/api/v1/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.accessToken").isNotEmpty())
                    .andExpect(jsonPath("$.user.username").value(testUsername));

            // Attempt login with wrong password -> should be rejected
            LoginRequest wrongReq = new LoginRequest(testUsername, "WrongPassword@123");
            mockMvc.perform(post("/api/v1/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(wrongReq)))
                    .andExpect(status().isUnauthorized());
        } finally {
            userRepository.findByUsernameIgnoreCase(testUsername).ifPresent(userRepository::delete);
        }
    }
}
