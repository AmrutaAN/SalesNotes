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
    @DisplayName("Should request password reset link and reset password using token")
    void shouldResetPasswordUsingToken() throws Exception {
        com.salesnotes.dto.ForgotPasswordRequestDto linkReq = new com.salesnotes.dto.ForgotPasswordRequestDto("companyemployee");

        MvcResult result = mockMvc.perform(post("/api/v1/auth/forgot-password/request-link")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(linkReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.resetUrl").isNotEmpty())
                .andReturn();

        String token = objectMapper.readTree(result.getResponse().getContentAsString()).get("token").asText();

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
}
