package com.salesnotes.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.salesnotes.dto.*;
import com.salesnotes.entity.*;
import com.salesnotes.repository.*;
import com.salesnotes.security.UserPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class ClipboardService {

    private final ClipboardSessionRepository clipboardSessionRepository;
    private final ClipboardEntryRepository clipboardEntryRepository;
    private final ExportRecordRepository exportRecordRepository;
    private final LogEntryRepository logEntryRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    public ClipboardService(
            ClipboardSessionRepository clipboardSessionRepository,
            ClipboardEntryRepository clipboardEntryRepository,
            ExportRecordRepository exportRecordRepository,
            LogEntryRepository logEntryRepository,
            UserRepository userRepository
    ) {
        this.clipboardSessionRepository = clipboardSessionRepository;
        this.clipboardEntryRepository = clipboardEntryRepository;
        this.exportRecordRepository = exportRecordRepository;
        this.logEntryRepository = logEntryRepository;
        this.userRepository = userRepository;
        this.objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    }

    public ClipboardSession getOrCreateSession(User user) {
        return clipboardSessionRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    ClipboardSession newSession = new ClipboardSession(user, user.getOrganisation());
                    newSession.setStatus("active");
                    newSession.setEntryCount(0);
                    return clipboardSessionRepository.save(newSession);
                });
    }

    @Transactional(readOnly = true)
    public ClipboardSessionResponseDto getClipboard(UserPrincipal currentUser) {
        User user = getUserOrThrow(currentUser.getId());
        ClipboardSession session = getOrCreateSession(user);
        List<ClipboardEntry> entries = clipboardEntryRepository.findByClipboardSessionIdOrderByStagedAtDesc(session.getId());
        return toSessionResponseDto(session, entries);
    }

    public ClipboardSessionResponseDto stageEntry(String logEntryId, UserPrincipal currentUser) {
        User user = getUserOrThrow(currentUser.getId());
        ClipboardSession session = getOrCreateSession(user);

        LogEntry logEntry = logEntryRepository.findById(logEntryId)
                .orElseThrow(() -> new IllegalArgumentException("Log entry not found: " + logEntryId));

        if (Boolean.TRUE.equals(logEntry.getDeleted())) {
            throw new IllegalArgumentException("Cannot stage deleted log entry");
        }

        // Validate organisation match unless sysadmin
        if (currentUser.getRole() != UserRole.SYSADMIN &&
                user.getOrganisation() != null &&
                logEntry.getOrganisation() != null &&
                !user.getOrganisation().getId().equals(logEntry.getOrganisation().getId())) {
            throw new SecurityException("Cannot stage log entry belonging to another organisation");
        }

        // Idempotent: check if already in clipboard
        if (!clipboardEntryRepository.existsByClipboardSessionIdAndLogEntryId(session.getId(), logEntryId)) {
            ClipboardEntry newEntry = new ClipboardEntry(session, logEntry);
            clipboardEntryRepository.save(newEntry);

            List<ClipboardEntry> currentEntries = clipboardEntryRepository.findByClipboardSessionIdOrderByStagedAtDesc(session.getId());
            session.setEntryCount(currentEntries.size());
            session.setStatus("active");
            session.setClearedAt(null);
            session.setUpdatedAt(LocalDateTime.now());
            clipboardSessionRepository.save(session);
        }

        List<ClipboardEntry> allEntries = clipboardEntryRepository.findByClipboardSessionIdOrderByStagedAtDesc(session.getId());
        return toSessionResponseDto(session, allEntries);
    }

    public ClipboardSessionResponseDto bulkStageEntries(List<String> logEntryIds, UserPrincipal currentUser) {
        if (logEntryIds == null || logEntryIds.isEmpty()) {
            return getClipboard(currentUser);
        }

        User user = getUserOrThrow(currentUser.getId());
        ClipboardSession session = getOrCreateSession(user);

        for (String logEntryId : logEntryIds) {
            Optional<LogEntry> logEntryOpt = logEntryRepository.findById(logEntryId);
            if (logEntryOpt.isPresent()) {
                LogEntry logEntry = logEntryOpt.get();
                if (!Boolean.TRUE.equals(logEntry.getDeleted())) {
                    if (!clipboardEntryRepository.existsByClipboardSessionIdAndLogEntryId(session.getId(), logEntryId)) {
                        ClipboardEntry newEntry = new ClipboardEntry(session, logEntry);
                        clipboardEntryRepository.save(newEntry);
                    }
                }
            }
        }

        List<ClipboardEntry> allEntries = clipboardEntryRepository.findByClipboardSessionIdOrderByStagedAtDesc(session.getId());
        session.setEntryCount(allEntries.size());
        session.setStatus("active");
        session.setClearedAt(null);
        session.setUpdatedAt(LocalDateTime.now());
        clipboardSessionRepository.save(session);

        return toSessionResponseDto(session, allEntries);
    }

    public ClipboardSessionResponseDto unstageEntry(String clipboardEntryId, UserPrincipal currentUser) {
        User user = getUserOrThrow(currentUser.getId());
        ClipboardSession session = getOrCreateSession(user);

        ClipboardEntry entry = clipboardEntryRepository.findById(clipboardEntryId)
                .orElseThrow(() -> new IllegalArgumentException("Clipboard entry not found: " + clipboardEntryId));

        if (!entry.getClipboardSession().getId().equals(session.getId())) {
            throw new SecurityException("Cannot unstage entry belonging to another user's session");
        }

        clipboardEntryRepository.delete(entry);

        List<ClipboardEntry> remainingEntries = clipboardEntryRepository.findByClipboardSessionIdOrderByStagedAtDesc(session.getId());
        session.setEntryCount(remainingEntries.size());
        session.setUpdatedAt(LocalDateTime.now());
        clipboardSessionRepository.save(session);

        return toSessionResponseDto(session, remainingEntries);
    }

    public ClipboardSessionResponseDto unstageByLogEntryId(String logEntryId, UserPrincipal currentUser) {
        User user = getUserOrThrow(currentUser.getId());
        ClipboardSession session = getOrCreateSession(user);

        clipboardEntryRepository.deleteByClipboardSessionIdAndLogEntryId(session.getId(), logEntryId);

        List<ClipboardEntry> remainingEntries = clipboardEntryRepository.findByClipboardSessionIdOrderByStagedAtDesc(session.getId());
        session.setEntryCount(remainingEntries.size());
        session.setUpdatedAt(LocalDateTime.now());
        clipboardSessionRepository.save(session);

        return toSessionResponseDto(session, remainingEntries);
    }

    public ClipboardSessionResponseDto clearClipboard(UserPrincipal currentUser) {
        User user = getUserOrThrow(currentUser.getId());
        ClipboardSession session = getOrCreateSession(user);

        clipboardEntryRepository.deleteByClipboardSessionId(session.getId());

        session.setEntryCount(0);
        session.setStatus("cleared");
        session.setClearedAt(LocalDateTime.now());
        session.setUpdatedAt(LocalDateTime.now());
        clipboardSessionRepository.save(session);

        return toSessionResponseDto(session, Collections.emptyList());
    }

    public ExportResponseDto exportClipboard(ExportRequestDto request, UserPrincipal currentUser) {
        User user = getUserOrThrow(currentUser.getId());
        ClipboardSession session = getOrCreateSession(user);

        List<ClipboardEntry> allStagedEntries = clipboardEntryRepository.findByClipboardSessionIdOrderByStagedAtDesc(session.getId());
        if (allStagedEntries.isEmpty()) {
            throw new IllegalStateException("Clipboard is empty; no entries available to export");
        }

        List<ClipboardEntry> toExport = allStagedEntries;
        if (request.getEntryIds() != null && !request.getEntryIds().isEmpty()) {
            Set<String> targetIds = new HashSet<>(request.getEntryIds());
            toExport = allStagedEntries.stream()
                    .filter(e -> targetIds.contains(e.getId()) || targetIds.contains(e.getLogEntry().getId()))
                    .collect(Collectors.toList());

            if (toExport.isEmpty()) {
                throw new IllegalArgumentException("None of the specified entry IDs exist in the active clipboard");
            }
        }

        List<ClipboardEntryResponseDto> entryDtos = toExport.stream()
                .map(this::toEntryResponseDto)
                .collect(Collectors.toList());

        // Build distinct client profiles snapshot
        Map<String, Map<String, Object>> clientProfiles = new HashMap<>();
        for (ClipboardEntry ce : toExport) {
            LogEntry le = ce.getLogEntry();
            if (le.getClientNote() != null && le.getClientNote().getClient() != null) {
                Client c = le.getClientNote().getClient();
                clientProfiles.putIfAbsent(c.getId(), Map.of(
                        "clientId", c.getId(),
                        "name", c.getName(),
                        "companyName", c.getCompany() != null ? c.getCompany() : "",
                        "phone", c.getPhone() != null ? c.getPhone() : "",
                        "email", c.getEmail() != null ? c.getEmail() : ""
                ));
            }
        }

        // Construct immutable JSON snapshot payload
        Map<String, Object> payloadMap = new LinkedHashMap<>();
        payloadMap.put("targetModule", request.getTargetModule());
        payloadMap.put("externalRefId", request.getExternalRefId());
        payloadMap.put("notes", request.getNotes());
        payloadMap.put("exportedAt", LocalDateTime.now().toString());
        payloadMap.put("entryCount", entryDtos.size());
        payloadMap.put("exportedBy", Map.of(
                "userId", user.getId(),
                "username", user.getUsername(),
                "fullname", user.getFullname() != null ? user.getFullname() : "",
                "email", user.getEmail() != null ? user.getEmail() : ""
        ));
        payloadMap.put("organisationId", user.getOrganisation() != null ? user.getOrganisation().getId() : null);
        payloadMap.put("clients", new ArrayList<>(clientProfiles.values()));
        payloadMap.put("entries", entryDtos);

        String jsonPayload;
        try {
            jsonPayload = objectMapper.writeValueAsString(payloadMap);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize export payload to JSON", e);
        }

        ExportRecord exportRecord = new ExportRecord(
                session,
                user,
                user.getOrganisation(),
                request.getTargetModule(),
                jsonPayload,
                entryDtos.size()
        );
        exportRecord.setExternalRefId(request.getExternalRefId());
        exportRecord.setStatus("success");
        ExportRecord savedExport = exportRecordRepository.save(exportRecord);

        // Clear exported entries if clearAfterExport is true or null (default)
        if (request.getClearAfterExport() == null || Boolean.TRUE.equals(request.getClearAfterExport())) {
            List<String> exportedEntryIds = toExport.stream().map(ClipboardEntry::getId).collect(Collectors.toList());
            clipboardEntryRepository.deleteBySessionIdAndIds(session.getId(), exportedEntryIds);

            List<ClipboardEntry> remaining = clipboardEntryRepository.findByClipboardSessionIdOrderByStagedAtDesc(session.getId());
            session.setEntryCount(remaining.size());
            if (remaining.isEmpty()) {
                session.setStatus("exported");
                session.setClearedAt(LocalDateTime.now());
            }
            session.setUpdatedAt(LocalDateTime.now());
            clipboardSessionRepository.save(session);
        }

        return toExportResponseDto(savedExport, entryDtos);
    }

    @Transactional(readOnly = true)
    public List<ExportResponseDto> getExportHistory(UserPrincipal currentUser) {
        User user = getUserOrThrow(currentUser.getId());
        List<ExportRecord> records;
        if (currentUser.getRole() == UserRole.SYSADMIN || currentUser.getRole() == UserRole.COMPANYADMIN) {
            records = exportRecordRepository.findByOrganisationIdOrderByExportedAtDesc(user.getOrganisation().getId());
        } else {
            records = exportRecordRepository.findByExportedByIdOrderByExportedAtDesc(user.getId());
        }

        return records.stream()
                .map(rec -> toExportResponseDto(rec, null))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ExportResponseDto getExportById(String exportId, UserPrincipal currentUser) {
        ExportRecord record = exportRecordRepository.findById(exportId)
                .orElseThrow(() -> new IllegalArgumentException("Export record not found: " + exportId));

        User user = getUserOrThrow(currentUser.getId());
        if (currentUser.getRole() != UserRole.SYSADMIN &&
                user.getOrganisation() != null &&
                record.getOrganisation() != null &&
                !user.getOrganisation().getId().equals(record.getOrganisation().getId())) {
            throw new SecurityException("Cannot access export record belonging to another organisation");
        }

        return toExportResponseDto(record, null);
    }

    private User getUserOrThrow(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
    }

    private ClipboardSessionResponseDto toSessionResponseDto(ClipboardSession session, List<ClipboardEntry> entries) {
        ClipboardSessionResponseDto dto = new ClipboardSessionResponseDto();
        dto.setId(session.getId());
        dto.setUserId(session.getUser() != null ? session.getUser().getId() : null);
        dto.setStatus(session.getStatus());
        dto.setEntryCount(entries != null ? entries.size() : session.getEntryCount());
        dto.setExpiresAt(session.getExpiresAt());
        dto.setClearedAt(session.getClearedAt());
        dto.setCreatedAt(session.getCreatedAt());
        dto.setUpdatedAt(session.getUpdatedAt());

        if (entries != null) {
            dto.setEntries(entries.stream().map(this::toEntryResponseDto).collect(Collectors.toList()));
        }
        return dto;
    }

    private ClipboardEntryResponseDto toEntryResponseDto(ClipboardEntry entry) {
        ClipboardEntryResponseDto dto = new ClipboardEntryResponseDto();
        dto.setId(entry.getId());
        dto.setStagedAt(entry.getStagedAt());

        LogEntry le = entry.getLogEntry();
        if (le != null) {
            dto.setLogEntryId(le.getId());
            dto.setInteractionType(le.getInteractionType());
            dto.setInteractionDate(le.getInteractionDate());
            dto.setInteractionTime(le.getInteractionTime());
            dto.setTextContent(le.getTextContent());
            dto.setPriority(le.getPriority());
            dto.setHasAttachments(le.getHasAttachments());
            dto.setHasVoiceNote(le.getHasVoiceNote());
            dto.setHasTranscription(le.getHasTranscription());

            if (le.getClientNote() != null) {
                dto.setClientNoteId(le.getClientNote().getId());
                Client c = le.getClientNote().getClient();
                if (c != null) {
                    dto.setClientId(c.getId());
                    dto.setClientName(c.getName());
                    dto.setClientCompany(c.getCompany());
                    dto.setDeepLinkUrl("/clients/" + c.getId() + "/notes?entry=" + le.getId());
                }
            }

            if (le.getTag() != null) {
                Tag tag = le.getTag();
                dto.setTagId(tag.getId());
                dto.setTagCode(tag.getCode());
                dto.setTagName(tag.getLabel());
                dto.setTagColor(tag.getColorHex());
            }
        }

        return dto;
    }

    private ExportResponseDto toExportResponseDto(ExportRecord record, List<ClipboardEntryResponseDto> exportedEntries) {
        ExportResponseDto dto = new ExportResponseDto();
        dto.setId(record.getId());
        dto.setClipboardSessionId(record.getClipboardSession() != null ? record.getClipboardSession().getId() : null);
        dto.setExportedById(record.getExportedBy() != null ? record.getExportedBy().getId() : null);
        dto.setExportedByName(record.getExportedBy() != null ? record.getExportedBy().getFullname() : null);
        dto.setTargetModule(record.getTargetModule());
        dto.setExternalRefId(record.getExternalRefId());
        dto.setEntryCount(record.getEntryCount());
        dto.setStatus(record.getStatus());
        dto.setErrorMessage(record.getErrorMessage());
        dto.setExportedAt(record.getExportedAt());
        dto.setExportPayload(record.getExportPayload());
        dto.setExportedEntries(exportedEntries);
        return dto;
    }
}
