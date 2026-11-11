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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("devlocal")
class VoiceTranscriptionTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String employeeToken;
    private String clientNoteId;
    private String logEntryId;

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

        // Create client
        String clientPayload = """
                {
                    "name": "Apex Engineering Ltd",
                    "company": "Apex Engineering Solutions",
                    "industryTag": "Manufacturing",
                    "email": "procurement@apexeng.com",
                    "phone": "+1555666777",
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

        // Fetch ORDER tag ID
        MvcResult tagResult = mockMvc.perform(get("/api/v1/tags")
                        .header("Authorization", employeeToken))
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

        // Create Log Entry
        LogEntryRequestDto entryReq = new LogEntryRequestDto();
        entryReq.setInteractionType("field_visit");
        entryReq.setInteractionDate(LocalDate.now());
        entryReq.setTextContent("Met with Plant Manager at site.");
        entryReq.setTagId(orderTagId);
        entryReq.setPriority(true);

        MvcResult entryResult = mockMvc.perform(post("/api/v1/notes/" + clientNoteId + "/entries")
                        .header("Authorization", employeeToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(entryReq)))
                .andExpect(status().isCreated())
                .andReturn();

        LogEntryResponseDto entry = objectMapper.readValue(entryResult.getResponse().getContentAsString(), LogEntryResponseDto.class);
        this.logEntryId = entry.getId();
    }

    @Test
    @DisplayName("Should upload voice note audio file and allow public streaming")
    void shouldUploadVoiceNoteAndStreamAudio() throws Exception {
        MockMultipartFile audioFile = new MockMultipartFile(
                "file",
                "field_visit_memo.m4a",
                "audio/mp4",
                "FAKE_AUDIO_STREAM_BINARY_DATA".getBytes()
        );

        MvcResult uploadResult = mockMvc.perform(multipart("/api/v1/notes/" + clientNoteId + "/entries/" + logEntryId + "/audio")
                        .file(audioFile)
                        .param("durationSeconds", "42")
                        .header("Authorization", employeeToken))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.attachmentType").value("voice_note"))
                .andExpect(jsonPath("$.fileName").value("field_visit_memo.m4a"))
                .andExpect(jsonPath("$.durationSeconds").value(42))
                .andExpect(jsonPath("$.publicUrl").exists())
                .andReturn();

        AttachmentResponseDto attachment = objectMapper.readValue(uploadResult.getResponse().getContentAsString(), AttachmentResponseDto.class);
        assertThat(attachment.getId()).isNotBlank();

        // Verify parent log entry now has hasVoiceNote = true
        mockMvc.perform(get("/api/v1/entries/" + logEntryId)
                        .header("Authorization", employeeToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hasVoiceNote").value(true))
                .andExpect(jsonPath("$.hasAttachments").value(true));

        // Verify audio streaming URL returns content
        mockMvc.perform(get(attachment.getPublicUrl()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should execute two-step voice transcription workflow: request, review, and confirm")
    void shouldExecuteVoiceTranscriptionWorkflow() throws Exception {
        // 1. Upload audio note
        MockMultipartFile audioFile = new MockMultipartFile(
                "file",
                "sales_brief.m4a",
                "audio/mp4",
                "SIMULATED_SPEECH_DATA".getBytes()
        );

        MvcResult uploadResult = mockMvc.perform(multipart("/api/v1/notes/" + clientNoteId + "/entries/" + logEntryId + "/audio")
                        .file(audioFile)
                        .param("durationSeconds", "95")
                        .header("Authorization", employeeToken))
                .andExpect(status().isCreated())
                .andReturn();
        AttachmentResponseDto attachment = objectMapper.readValue(uploadResult.getResponse().getContentAsString(), AttachmentResponseDto.class);

        // 2. Request AI Transcription
        VoiceTranscriptionRequestDto transcriptionReq = new VoiceTranscriptionRequestDto(attachment.getId(), "en");
        MvcResult requestResult = mockMvc.perform(post("/api/v1/transcriptions/request")
                        .header("Authorization", employeeToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transcriptionReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("awaiting_confirmation"))
                .andExpect(jsonPath("$.aiModelUsed").value("whisper-large-v3"))
                .andExpect(jsonPath("$.rawTranscript").isNotEmpty())
                .andReturn();

        VoiceTranscriptionResponseDto transcription = objectMapper.readValue(requestResult.getResponse().getContentAsString(), VoiceTranscriptionResponseDto.class);
        assertThat(transcription.getRawTranscript()).isNotBlank();

        // 3. User reviews in structured confirmation screen and confirms edited transcript
        String reviewedText = "Confirmed order for 200 industrial valves with Q3 tiered pricing schedule.";
        TranscriptionConfirmRequestDto confirmReq = new TranscriptionConfirmRequestDto(reviewedText, true);

        mockMvc.perform(post("/api/v1/transcriptions/" + transcription.getId() + "/confirm")
                        .header("Authorization", employeeToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(confirmReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("confirmed"))
                .andExpect(jsonPath("$.finalTranscript").value(reviewedText))
                .andExpect(jsonPath("$.confirmedAt").isNotEmpty());

        // 4. Verify parent log entry text was automatically updated with confirmed transcript
        mockMvc.perform(get("/api/v1/entries/" + logEntryId)
                        .header("Authorization", employeeToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hasTranscription").value(true))
                .andExpect(jsonPath("$.textContent").value(org.hamcrest.Matchers.containsString(reviewedText)));
    }

    @Test
    @DisplayName("Should discard transcription when user rejects it")
    void shouldDiscardTranscription() throws Exception {
        MockMultipartFile audioFile = new MockMultipartFile(
                "file",
                "noise.m4a",
                "audio/mp4",
                "BACKGROUND_NOISE".getBytes()
        );

        MvcResult uploadResult = mockMvc.perform(multipart("/api/v1/notes/" + clientNoteId + "/entries/" + logEntryId + "/audio")
                        .file(audioFile)
                        .header("Authorization", employeeToken))
                .andExpect(status().isCreated())
                .andReturn();
        AttachmentResponseDto attachment = objectMapper.readValue(uploadResult.getResponse().getContentAsString(), AttachmentResponseDto.class);

        VoiceTranscriptionRequestDto transcriptionReq = new VoiceTranscriptionRequestDto(attachment.getId());
        MvcResult requestResult = mockMvc.perform(post("/api/v1/transcriptions/request")
                        .header("Authorization", employeeToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transcriptionReq)))
                .andExpect(status().isCreated())
                .andReturn();

        VoiceTranscriptionResponseDto transcription = objectMapper.readValue(requestResult.getResponse().getContentAsString(), VoiceTranscriptionResponseDto.class);

        // Discard
        mockMvc.perform(post("/api/v1/transcriptions/" + transcription.getId() + "/discard")
                        .header("Authorization", employeeToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("discarded"))
                .andExpect(jsonPath("$.discardedAt").isNotEmpty());
    }
}
