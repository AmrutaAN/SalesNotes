package com.salesnotes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.salesnotes.dto.LoginRequest;
import com.salesnotes.dto.LoginResponse;
import com.salesnotes.dto.RoleRequestDto;
import org.junit.jupiter.api.BeforeEach;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("devlocal")
class RoleTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String superUserToken;

    @BeforeEach
    void setUp() throws Exception {
        // Authenticate as sysadmin (which has role SuperUser)
        LoginRequest req = new LoginRequest("sysadmin", "SysAdmin@123");
        MvcResult res = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andReturn();

        LoginResponse login = objectMapper.readValue(res.getResponse().getContentAsString(), LoginResponse.class);
        this.superUserToken = "Bearer " + login.getAccessToken();

        // Verify sysadmin role is SuperUser
        assertThat(login.getUser().getRole().getValue()).isEqualTo("SuperUser");
    }

    @Test
    @DisplayName("Should list system roles including SuperUser, Sysadmin, companyadmin, and companyemployee")
    void shouldListSystemRoles() throws Exception {
        mockMvc.perform(get("/api/v1/roles")
                        .header("Authorization", superUserToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[?(@.code == 'SuperUser')]").exists())
                .andExpect(jsonPath("$[?(@.code == 'Sysadmin')]").exists())
                .andExpect(jsonPath("$[?(@.code == 'companyadmin')]").exists())
                .andExpect(jsonPath("$[?(@.code == 'companyemployee')]").exists());
    }

    @Test
    @DisplayName("Should create new custom role and edit role details")
    void shouldCreateAndEditCustomRole() throws Exception {
        String roleCode = "RegionalManager_" + System.currentTimeMillis();
        RoleRequestDto createReq = new RoleRequestDto(roleCode, "Regional Sales Manager", "Oversees state-level sales teams");

        // 1. Create custom role
        MvcResult createRes = mockMvc.perform(post("/api/v1/roles")
                        .header("Authorization", superUserToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(roleCode))
                .andExpect(jsonPath("$.name").value("Regional Sales Manager"))
                .andExpect(jsonPath("$.isSystem").value(false))
                .andReturn();

        String roleId = objectMapper.readTree(createRes.getResponse().getContentAsString()).get("id").asText();

        // 2. Edit the role
        RoleRequestDto updateReq = new RoleRequestDto(roleCode, "Senior Regional Sales Manager", "Updated description for regional head");
        mockMvc.perform(put("/api/v1/roles/" + roleId)
                        .header("Authorization", superUserToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Senior Regional Sales Manager"))
                .andExpect(jsonPath("$.description").value("Updated description for regional head"));

        // 3. Delete the custom role
        mockMvc.perform(delete("/api/v1/roles/" + roleId)
                        .header("Authorization", superUserToken))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Should assign role to user and update role dynamically")
    void shouldAssignRoleToUser() throws Exception {
        String testUsername = "fieldrep_" + System.currentTimeMillis();
        com.salesnotes.dto.UserRequest createReq = new com.salesnotes.dto.UserRequest();
        createReq.setUsername(testUsername);
        createReq.setFullname("Test Field Rep");
        createReq.setEmail(testUsername + "@salesnotes.test");
        createReq.setPassword("RepPass@123");
        createReq.setRole(com.salesnotes.entity.UserRole.COMPANYEMPLOYEE);

        // 1. Create user with role companyemployee
        MvcResult userRes = mockMvc.perform(post("/api/v1/users")
                        .header("Authorization", superUserToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.role").value("companyemployee"))
                .andReturn();

        String userId = objectMapper.readTree(userRes.getResponse().getContentAsString()).get("id").asText();

        // 2. Assign new role companyadmin via /api/v1/users/{id}/role
        java.util.Map<String, String> assignRoleBody = java.util.Map.of("role", "companyadmin");
        mockMvc.perform(patch("/api/v1/users/" + userId + "/role")
                        .header("Authorization", superUserToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(assignRoleBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("companyadmin"));

        // 3. Verify fetched user has updated role
        mockMvc.perform(get("/api/v1/users/" + userId)
                        .header("Authorization", superUserToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("companyadmin"));

        // 4. Clean up test user
        mockMvc.perform(delete("/api/v1/users/" + userId)
                        .header("Authorization", superUserToken))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Should allow multiple users with the same email (non-unique email)")
    void shouldAllowMultipleUsersWithSameEmail() throws Exception {
        String sharedEmail = "shared_team_" + System.currentTimeMillis() + "@salesnotes.test";

        // 1. Create first user with shared email
        com.salesnotes.dto.UserRequest user1 = new com.salesnotes.dto.UserRequest();
        user1.setUsername("user_a_" + System.currentTimeMillis());
        user1.setFullname("User Alpha");
        user1.setEmail(sharedEmail);
        user1.setPassword("Pass@123");
        user1.setRole(com.salesnotes.entity.UserRole.COMPANYEMPLOYEE);

        MvcResult res1 = mockMvc.perform(post("/api/v1/users")
                        .header("Authorization", superUserToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user1)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value(sharedEmail))
                .andReturn();
        String id1 = objectMapper.readTree(res1.getResponse().getContentAsString()).get("id").asText();

        // 2. Create second user with DIFFERENT username but SAME email
        com.salesnotes.dto.UserRequest user2 = new com.salesnotes.dto.UserRequest();
        user2.setUsername("user_b_" + System.currentTimeMillis());
        user2.setFullname("User Beta");
        user2.setEmail(sharedEmail);
        user2.setPassword("Pass@456");
        user2.setRole(com.salesnotes.entity.UserRole.COMPANYEMPLOYEE);

        MvcResult res2 = mockMvc.perform(post("/api/v1/users")
                        .header("Authorization", superUserToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user2)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value(sharedEmail))
                .andReturn();
        String id2 = objectMapper.readTree(res2.getResponse().getContentAsString()).get("id").asText();

        // Clean up
        mockMvc.perform(delete("/api/v1/users/" + id1).header("Authorization", superUserToken)).andExpect(status().isNoContent());
        mockMvc.perform(delete("/api/v1/users/" + id2).header("Authorization", superUserToken)).andExpect(status().isNoContent());
    }
}
