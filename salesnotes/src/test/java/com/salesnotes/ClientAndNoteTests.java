package com.salesnotes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.salesnotes.dto.*;
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

import java.time.LocalDate;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("devlocal")
class ClientAndNoteTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String authToken;

    @BeforeEach
    void setUp() throws Exception {
        LoginRequest req = new LoginRequest("companyadmin", "Admin@123");
        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andReturn();

        LoginResponse res = objectMapper.readValue(result.getResponse().getContentAsString(), LoginResponse.class);
        this.authToken = "Bearer " + res.getAccessToken();
    }

    @Test
    @DisplayName("Should create client, auto-generate ClientNote container, and log an ORDER entry")
    void shouldCreateClientAndLogEntry() throws Exception {
        // 1. Create Client
        ClientRequestDto clientReq = new ClientRequestDto();
        clientReq.setName("Horizon Power Systems");
        clientReq.setCompany("Horizon Global");
        clientReq.setIndustryTag("Renewable Energy");
        clientReq.setPriorityFlag(true);

        MvcResult clientResult = mockMvc.perform(post("/api/v1/clients")
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clientReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.clientNoteId").isNotEmpty())
                .andReturn();

        ClientResponseDto client = objectMapper.readValue(clientResult.getResponse().getContentAsString(), ClientResponseDto.class);
        String clientNoteId = client.getClientNoteId();
        assertThat(clientNoteId).isNotNull();

        // 2. Fetch system tags to find ORDER tag
        MvcResult tagResult = mockMvc.perform(get("/api/v1/tags")
                        .header("Authorization", authToken))
                .andExpect(status().isOk())
                .andReturn();

        TagResponseDto[] tags = objectMapper.readValue(tagResult.getResponse().getContentAsString(), TagResponseDto[].class);
        String orderTagId = null;
        for (TagResponseDto t : tags) {
            if ("ORDER".equalsIgnoreCase(t.getCode())) {
                orderTagId = t.getId();
                break;
            }
        }

        // 3. Create Log Entry under client note space
        LogEntryRequestDto entryReq = new LogEntryRequestDto();
        entryReq.setInteractionType("field_visit");
        entryReq.setInteractionDate(LocalDate.now());
        entryReq.setTextContent("Met with Procurement Director. Client confirmed intent to place order for 200 battery modules.");
        entryReq.setTagId(orderTagId);
        entryReq.setPriority(true);

        MvcResult entryResult = mockMvc.perform(post("/api/v1/notes/" + clientNoteId + "/entries")
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(entryReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.tagCode").value("ORDER"))
                .andExpect(jsonPath("$.priority").value(true))
                .andReturn();

        LogEntryResponseDto entry = objectMapper.readValue(entryResult.getResponse().getContentAsString(), LogEntryResponseDto.class);

        // 4. Update Tag to COMPLAINT
        String complaintTagId = null;
        for (TagResponseDto t : tags) {
            if ("COMPLAINT".equalsIgnoreCase(t.getCode())) {
                complaintTagId = t.getId();
                break;
            }
        }

        mockMvc.perform(patch("/api/v1/entries/" + entry.getId() + "/tag")
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new TagUpdateDto(complaintTagId))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tagCode").value("COMPLAINT"));

        // 5. Add Web Link attachment
        mockMvc.perform(post("/api/v1/entries/" + entry.getId() + "/attachments/link")
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("url", "https://salesnotes.io/specs/battery-module.pdf", "title", "Product Specs"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.linkTitle").value("Product Specs"));

        // 6. Verify Client Summary reflects interaction and note counts
        mockMvc.perform(get("/api/v1/clients/" + client.getId() + "/summary")
                        .header("Authorization", authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalEntriesCount").value(1))
                .andExpect(jsonPath("$.openComplaintsCount").value(1));
    }
}
