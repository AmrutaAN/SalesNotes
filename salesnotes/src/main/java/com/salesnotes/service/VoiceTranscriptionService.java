package com.salesnotes.service;

import com.salesnotes.dto.*;
import com.salesnotes.entity.*;
import com.salesnotes.repository.*;
import com.salesnotes.security.UserPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class VoiceTranscriptionService {

    private final VoiceTranscriptionRepository voiceTranscriptionRepository;
    private final LogEntryAttachmentRepository logEntryAttachmentRepository;
    private final ItemAttachmentRepository itemAttachmentRepository;
    private final LogEntryRepository logEntryRepository;
    private final UserRepository userRepository;
    private final AudioStorageService audioStorageService;

    public VoiceTranscriptionService(
            VoiceTranscriptionRepository voiceTranscriptionRepository,
            LogEntryAttachmentRepository logEntryAttachmentRepository,
            ItemAttachmentRepository itemAttachmentRepository,
            LogEntryRepository logEntryRepository,
            UserRepository userRepository,
            AudioStorageService audioStorageService
    ) {
        this.voiceTranscriptionRepository = voiceTranscriptionRepository;
        this.logEntryAttachmentRepository = logEntryAttachmentRepository;
        this.itemAttachmentRepository = itemAttachmentRepository;
        this.logEntryRepository = logEntryRepository;
        this.userRepository = userRepository;
        this.audioStorageService = audioStorageService;
    }

    public AttachmentResponseDto uploadVoiceNote(
            String logEntryId,
            MultipartFile file,
            Integer durationSeconds,
            UserPrincipal currentUser
    ) {
        User user = getUserOrThrow(currentUser.getId());

        LogEntry logEntry = logEntryRepository.findById(logEntryId)
                .orElseThrow(() -> new IllegalArgumentException("Log entry not found: " + logEntryId));

        if (Boolean.TRUE.equals(logEntry.getDeleted())) {
            throw new IllegalArgumentException("Cannot attach audio to a deleted log entry");
        }

        AudioStorageService.StorageResult storageResult = audioStorageService.store(file);

        LogEntryAttachment attachment = new LogEntryAttachment();
        attachment.setLogEntry(logEntry);
        attachment.setUploadedByUser(user);
        attachment.setAttachmentType("voice_note");
        attachment.setFileName(storageResult.getOriginalFileName());
        attachment.setStorageKey(storageResult.getStorageKey());
        attachment.setPublicUrl(storageResult.getPublicUrl());
        attachment.setMimeType(storageResult.getMimeType());
        attachment.setFileSizeBytes(storageResult.getFileSizeBytes());
        attachment.setDurationSeconds(durationSeconds != null ? durationSeconds : 30);

        LogEntryAttachment savedAttachment = logEntryAttachmentRepository.save(attachment);

        logEntry.setHasVoiceNote(true);
        logEntry.setHasAttachments(true);
        logEntryRepository.save(logEntry);

        return AttachmentResponseDto.fromEntity(savedAttachment);
    }

    public VoiceTranscriptionResponseDto requestTranscription(
            VoiceTranscriptionRequestDto request,
            UserPrincipal currentUser
    ) {
        User user = getUserOrThrow(currentUser.getId());

        if (request.getAttachmentId() != null && !request.getAttachmentId().trim().isEmpty()) {
            return transcribeLogEntryAttachment(request.getAttachmentId(), request.getLanguageHint(), user);
        } else if (request.getItemAttachmentId() != null && !request.getItemAttachmentId().trim().isEmpty()) {
            return transcribeItemAttachment(request.getItemAttachmentId(), request.getLanguageHint(), user);
        } else {
            throw new IllegalArgumentException("Either attachmentId or itemAttachmentId is required");
        }
    }

    private VoiceTranscriptionResponseDto transcribeLogEntryAttachment(String attachmentId, String languageHint, User user) {
        LogEntryAttachment attachment = logEntryAttachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new IllegalArgumentException("Log entry attachment not found: " + attachmentId));

        Optional<VoiceTranscription> existing = voiceTranscriptionRepository.findByLogEntryAttachmentId(attachmentId);
        if (existing.isPresent()) {
            VoiceTranscription vt = existing.get();
            if ("awaiting_confirmation".equals(vt.getStatus()) || "confirmed".equals(vt.getStatus())) {
                return VoiceTranscriptionResponseDto.fromEntity(vt);
            }
        }

        VoiceTranscription vt = existing.orElseGet(VoiceTranscription::new);
        vt.setRequestedBy(user);
        vt.setLogEntryAttachment(attachment);
        vt.setSourceLanguageHint(languageHint != null ? languageHint : "en");
        vt.setAiModelUsed("whisper-large-v3");
        vt.setStatus("processing");
        vt.setProcessingStartedAt(LocalDateTime.now());

        // Simulated AI voice-to-text pipeline inference
        String simulatedTranscript = generateSimulatedTranscript(attachment.getFileName(), attachment.getLogEntry());
        vt.setRawTranscript(simulatedTranscript);
        vt.setEditedTranscript(simulatedTranscript);
        vt.setProcessingCompletedAt(LocalDateTime.now());
        vt.setStatus("awaiting_confirmation");

        VoiceTranscription saved = voiceTranscriptionRepository.save(vt);
        return VoiceTranscriptionResponseDto.fromEntity(saved);
    }

    private VoiceTranscriptionResponseDto transcribeItemAttachment(String itemAttachmentId, String languageHint, User user) {
        ItemAttachment itemAttachment = itemAttachmentRepository.findById(itemAttachmentId)
                .orElseThrow(() -> new IllegalArgumentException("Item attachment not found: " + itemAttachmentId));

        Optional<VoiceTranscription> existing = voiceTranscriptionRepository.findByItemAttachmentId(itemAttachmentId);
        if (existing.isPresent()) {
            VoiceTranscription vt = existing.get();
            if ("awaiting_confirmation".equals(vt.getStatus()) || "confirmed".equals(vt.getStatus())) {
                return VoiceTranscriptionResponseDto.fromEntity(vt);
            }
        }

        VoiceTranscription vt = existing.orElseGet(VoiceTranscription::new);
        vt.setRequestedBy(user);
        vt.setItemAttachment(itemAttachment);
        vt.setSourceLanguageHint(languageHint != null ? languageHint : "en");
        vt.setAiModelUsed("whisper-large-v3");
        vt.setStatus("processing");
        vt.setProcessingStartedAt(LocalDateTime.now());

        String simulatedTranscript = "Product catalogue update voice instruction: Adjust Q2 minimum order quantity to 50 units for distributor tier.";
        vt.setRawTranscript(simulatedTranscript);
        vt.setEditedTranscript(simulatedTranscript);
        vt.setProcessingCompletedAt(LocalDateTime.now());
        vt.setStatus("awaiting_confirmation");

        VoiceTranscription saved = voiceTranscriptionRepository.save(vt);
        return VoiceTranscriptionResponseDto.fromEntity(saved);
    }

    public VoiceTranscriptionResponseDto confirmTranscription(
            String transcriptionId,
            TranscriptionConfirmRequestDto request,
            UserPrincipal currentUser
    ) {
        VoiceTranscription vt = voiceTranscriptionRepository.findById(transcriptionId)
                .orElseThrow(() -> new IllegalArgumentException("Voice transcription not found: " + transcriptionId));

        vt.setFinalTranscript(request.getFinalTranscript());
        vt.setEditedTranscript(request.getFinalTranscript());
        vt.setStatus("confirmed");
        vt.setConfirmedAt(LocalDateTime.now());

        if (Boolean.TRUE.equals(request.getApplyToNote()) && vt.getLogEntryAttachment() != null) {
            LogEntry logEntry = vt.getLogEntryAttachment().getLogEntry();
            if (logEntry != null) {
                String currentText = logEntry.getTextContent();
                if (currentText == null || currentText.trim().isEmpty()) {
                    logEntry.setTextContent(request.getFinalTranscript());
                } else {
                    logEntry.setTextContent(currentText + "\n\n[Voice Note Transcript]: " + request.getFinalTranscript());
                }
                logEntry.setHasTranscription(true);
                logEntryRepository.save(logEntry);
            }
        }

        VoiceTranscription saved = voiceTranscriptionRepository.save(vt);
        return VoiceTranscriptionResponseDto.fromEntity(saved);
    }

    public VoiceTranscriptionResponseDto discardTranscription(String transcriptionId, UserPrincipal currentUser) {
        VoiceTranscription vt = voiceTranscriptionRepository.findById(transcriptionId)
                .orElseThrow(() -> new IllegalArgumentException("Voice transcription not found: " + transcriptionId));

        vt.setStatus("discarded");
        vt.setDiscardedAt(LocalDateTime.now());

        VoiceTranscription saved = voiceTranscriptionRepository.save(vt);
        return VoiceTranscriptionResponseDto.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public VoiceTranscriptionResponseDto getTranscription(String id, UserPrincipal currentUser) {
        VoiceTranscription vt = voiceTranscriptionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Voice transcription not found: " + id));
        return VoiceTranscriptionResponseDto.fromEntity(vt);
    }

    @Transactional(readOnly = true)
    public VoiceTranscriptionResponseDto getByAttachmentId(String attachmentId, UserPrincipal currentUser) {
        VoiceTranscription vt = voiceTranscriptionRepository.findByLogEntryAttachmentId(attachmentId)
                .orElseThrow(() -> new IllegalArgumentException("No transcription found for attachment: " + attachmentId));
        return VoiceTranscriptionResponseDto.fromEntity(vt);
    }

    @Transactional(readOnly = true)
    public List<VoiceTranscriptionResponseDto> listUserTranscriptions(UserPrincipal currentUser) {
        return voiceTranscriptionRepository.findByRequestedByIdOrderByCreatedAtDesc(currentUser.getId())
                .stream()
                .map(VoiceTranscriptionResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    private User getUserOrThrow(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
    }

    private String generateSimulatedTranscript(String fileName, LogEntry logEntry) {
        String clientName = logEntry != null && logEntry.getClientNote() != null && logEntry.getClientNote().getClient() != null
                ? logEntry.getClientNote().getClient().getName()
                : "the client";

        String tagCode = logEntry != null && logEntry.getTag() != null ? logEntry.getTag().getCode() : "INFO";

        if ("ORDER".equalsIgnoreCase(tagCode)) {
            return "Voice memo during on-site visit with " + clientName + ": Confirmed order volume for upcoming quarter. Requested updated price quote including tiered volume discounts and estimated freight.";
        } else if ("COMPLAINT".equalsIgnoreCase(tagCode)) {
            return "Urgent voice recording regarding " + clientName + ": Received feedback about transit damages on pallet #4. Quality assurance team needs to inspect and issue replacement voucher.";
        } else {
            return "Recorded meeting summary with " + clientName + ": Discussed project timeline, technical specifications, and scheduled follow-up conference next Tuesday.";
        }
    }
}
