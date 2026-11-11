package com.salesnotes;

import com.fasterxml.jackson.databind.JsonNode;
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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("devlocal")
class ClipboardTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String employeeToken;
    private String clientNoteId;
    private String logEntryId1;
    private String logEntryId2;

    @BeforeEach
    void setUp() throws Exception {
        // Authenticate as companyemployee
        LoginRequest empReq = new LoginRequest("companyemployee", "User@123");
        MvcResult empRes = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(empReq)))
                .andExpect(status().isOk())
                .andReturn();
        LoginResponse empLogin = objectMapper.readValue(empRes.getResponse().getContentAsString(), LoginResponse.class);
        this.employeeToken = "Bearer " + empLogin.getAccessToken();

        // Ensure a clean clipboard before each test
        mockMvc.perform(delete("/api/v1/clipboard")
                .header("Authorization", employeeToken));

        // Create a dedicated client for note logging
        String clientPayload = """
                {
                    "name": "Global Logistics Corp",
                    "company": "Global Logistics LLC",
                    "industryTag": "Transportation",
                    "email": "contact@globallogistics.com",
                    "phone": "+1987654321",
                    "priorityFlag": true
                }
                """;

        MvcResult clientResult = mockMvc.perform(post("/api/v1/clients")
                        .header("Authorization", employeeToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(clientPayload))
                .andExpect(status().isCreated())
                .andReturn();

        ClientResponseDto client = objectMapper.readValue(clientResult.getResponse().getContentAsString(), ClientResponseDto.class);
        this.clientNoteId = client.getClientNoteId();

        // Fetch tags to get ORDER and COMPLAINT tag IDs
        MvcResult tagResult = mockMvc.perform(get("/api/v1/tags")
                        .header("Authorization", employeeToken))
                .andExpect(status().isOk())
                .andReturn();
        TagResponseDto[] tags = objectMapper.readValue(tagResult.getResponse().getContentAsString(), TagResponseDto[].class);
        String orderTagId = null;
        String complaintTagId = null;
        for (TagResponseDto t : tags) {
            if ("ORDER".equalsIgnoreCase(t.getCode())) {
                orderTagId = t.getId();
            } else if ("COMPLAINT".equalsIgnoreCase(t.getCode())) {
                complaintTagId = t.getId();
            }
        }

        // Create Note Entry 1 (Order tag)
        LogEntryRequestDto noteReq1 = new LogEntryRequestDto();
        noteReq1.setInteractionType("field_visit");
        noteReq1.setInteractionDate(LocalDate.now());
        noteReq1.setTextContent("Client requested quotation for 500 units of heavy duty valves.");
        noteReq1.setTagId(orderTagId);
        noteReq1.setPriority(true);

        MvcResult entry1Res = mockMvc.perform(post("/api/v1/notes/" + clientNoteId + "/entries")
                        .header("Authorization", employeeToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(noteReq1)))
                .andExpect(status().isCreated())
                .andReturn();
        LogEntryResponseDto entry1 = objectMapper.readValue(entry1Res.getResponse().getContentAsString(), LogEntryResponseDto.class);
        this.logEntryId1 = entry1.getId();

        // Create Note Entry 2 (Complaint tag)
        LogEntryRequestDto noteReq2 = new LogEntryRequestDto();
        noteReq2.setInteractionType("phone_call");
        noteReq2.setInteractionDate(LocalDate.now());
        noteReq2.setTextContent("Client reported delivery delay of shipment #48291.");
        noteReq2.setTagId(complaintTagId);
        noteReq2.setPriority(false);

        MvcResult entry2Res = mockMvc.perform(post("/api/v1/notes/" + clientNoteId + "/entries")
                        .header("Authorization", employeeToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(noteReq2)))
                .andExpect(status().isCreated())
                .andReturn();
        LogEntryResponseDto entry2 = objectMapper.readValue(entry2Res.getResponse().getContentAsString(), LogEntryResponseDto.class);
        this.logEntryId2 = entry2.getId();
    }

    @Test
    @DisplayName("Should stage log entries into clipboard and handle duplicate staging idempotently")
    void shouldStageEntriesIdempotently() throws Exception {
        // 1. Stage entry 1
        StageEntryRequestDto stageReq1 = new StageEntryRequestDto(logEntryId1);
        mockMvc.perform(post("/api/v1/clipboard/stage")
                        .header("Authorization", employeeToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(stageReq1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.entryCount").value(1))
                .andExpect(jsonPath("$.entries[0].logEntryId").value(logEntryId1))
                .andExpect(jsonPath("$.entries[0].tagCode").value("ORDER"))
                .andExpect(jsonPath("$.entries[0].clientName").value("Global Logistics Corp"))
                .andExpect(jsonPath("$.entries[0].deepLinkUrl").exists());

        // 2. Stage entry 1 again -> must be idempotent (no duplicate created)
        mockMvc.perform(post("/api/v1/clipboard/stage")
                        .header("Authorization", employeeToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(stageReq1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.entryCount").value(1));

        // 3. Stage entry 2
        StageEntryRequestDto stageReq2 = new StageEntryRequestDto(logEntryId2);
        mockMvc.perform(post("/api/v1/clipboard/stage")
                        .header("Authorization", employeeToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(stageReq2)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.entryCount").value(2));

        // 4. Verify GET /api/v1/clipboard contains both entries
        mockMvc.perform(get("/api/v1/clipboard")
                        .header("Authorization", employeeToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.entryCount").value(2))
                .andExpect(jsonPath("$.status").value("active"));
    }

    @Test
    @DisplayName("Should unstage specific entry and clear clipboard")
    void shouldUnstageAndClearClipboard() throws Exception {
        // Stage both entries using bulk staging
        StageEntryRequestDto bulkReq = new StageEntryRequestDto(List.of(logEntryId1, logEntryId2));
        mockMvc.perform(post("/api/v1/clipboard/bulk-stage")
                        .header("Authorization", employeeToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bulkReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.entryCount").value(2));

        // Unstage entry 1 by log entry ID
        mockMvc.perform(delete("/api/v1/clipboard/entries/by-log-entry/" + logEntryId1)
                        .header("Authorization", employeeToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.entryCount").value(1))
                .andExpect(jsonPath("$.entries[0].logEntryId").value(logEntryId2));

        // Clear entire clipboard
        mockMvc.perform(delete("/api/v1/clipboard")
                        .header("Authorization", employeeToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.entryCount").value(0))
                .andExpect(jsonPath("$.status").value("cleared"));
    }

    @Test
    @DisplayName("Should export staged entries to Sales Order module with deep links and snapshot payload")
    void shouldExportStagedEntries() throws Exception {
        // 1. Stage both entries
        StageEntryRequestDto bulkReq = new StageEntryRequestDto(List.of(logEntryId1, logEntryId2));
        mockMvc.perform(post("/api/v1/clipboard/bulk-stage")
                        .header("Authorization", employeeToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bulkReq)))
                .andExpect(status().isOk());

        // 2. Perform Export to 'sales_order' module
        ExportRequestDto exportReq = new ExportRequestDto();
        exportReq.setTargetModule("sales_order");
        exportReq.setExternalRefId("SO-2026-TEST-900");
        exportReq.setNotes("Exported from field rep weekly visit notes");
        exportReq.setClearAfterExport(true);

        MvcResult exportResult = mockMvc.perform(post("/api/v1/clipboard/export")
                        .header("Authorization", employeeToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(exportReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.targetModule").value("sales_order"))
                .andExpect(jsonPath("$.externalRefId").value("SO-2026-TEST-900"))
                .andExpect(jsonPath("$.entryCount").value(2))
                .andExpect(jsonPath("$.status").value("success"))
                .andReturn();

        ExportResponseDto exportResponse = objectMapper.readValue(exportResult.getResponse().getContentAsString(), ExportResponseDto.class);
        assertThat(exportResponse.getId()).isNotBlank();
        assertThat(exportResponse.getExportPayload()).isNotBlank();

        // Verify JSON snapshot payload contains deepLinkUrl and client metadata
        JsonNode payloadJson = objectMapper.readTree(exportResponse.getExportPayload());
        assertThat(payloadJson.get("targetModule").asText()).isEqualTo("sales_order");
        assertThat(payloadJson.get("entries").isArray()).isTrue();
        assertThat(payloadJson.get("entries").size()).isEqualTo(2);
        assertThat(payloadJson.get("entries").get(0).get("deepLinkUrl").asText()).contains("/clients/");

        // 3. Verify clipboard is now cleared after export
        mockMvc.perform(get("/api/v1/clipboard")
                        .header("Authorization", employeeToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.entryCount").value(0));

        // 4. Retrieve export record by ID
        mockMvc.perform(get("/api/v1/clipboard/exports/" + exportResponse.getId())
                        .header("Authorization", employeeToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(exportResponse.getId()))
                .andExpect(jsonPath("$.targetModule").value("sales_order"));

        // 5. Query export history list
        mockMvc.perform(get("/api/v1/clipboard/exports")
                        .header("Authorization", employeeToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].targetModule").value("sales_order"));
    }
}
