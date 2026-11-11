package com.salesnotes.service;

import com.salesnotes.dto.AttachmentResponseDto;
import com.salesnotes.dto.LogEntryRequestDto;
import com.salesnotes.dto.LogEntryResponseDto;
import com.salesnotes.entity.*;
import com.salesnotes.repository.*;
import com.salesnotes.security.UserPrincipal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LogEntryService {

    private final LogEntryRepository logEntryRepository;
    private final LogEntryAttachmentRepository attachmentRepository;
    private final ClientNoteRepository clientNoteRepository;
    private final ClientRepository clientRepository;
    private final TagRepository tagRepository;
    private final UserRepository userRepository;

    public LogEntryService(
            LogEntryRepository logEntryRepository,
            LogEntryAttachmentRepository attachmentRepository,
            ClientNoteRepository clientNoteRepository,
            ClientRepository clientRepository,
            TagRepository tagRepository,
            UserRepository userRepository
    ) {
        this.logEntryRepository = logEntryRepository;
        this.attachmentRepository = attachmentRepository;
        this.clientNoteRepository = clientNoteRepository;
        this.clientRepository = clientRepository;
        this.tagRepository = tagRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public Page<LogEntryResponseDto> listEntries(
            String clientNoteId,
            String tagId,
            Boolean priority,
            String search,
            Pageable pageable,
            UserPrincipal currentUser
    ) {
        List<LogEntry> entries = logEntryRepository
                .findByClientNoteIdAndDeletedFalseOrderByPriorityDescInteractionDateDescCreatedAtDesc(clientNoteId);

        List<LogEntryResponseDto> filtered = entries.stream()
                .filter(e -> {
                    if (tagId == null || tagId.trim().isEmpty()) return true;
                    return e.getTag() != null && tagId.equals(e.getTag().getId());
                })
                .filter(e -> {
                    if (priority == null) return true;
                    return priority.equals(e.getPriority());
                })
                .filter(e -> {
                    if (search == null || search.trim().isEmpty()) return true;
                    String term = search.trim().toLowerCase();
                    return e.getTextContent() != null && e.getTextContent().toLowerCase().contains(term);
                })
                .map(LogEntryResponseDto::fromEntity)
                .collect(Collectors.toList());

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), filtered.size());
        List<LogEntryResponseDto> pageContent = start <= end ? filtered.subList(start, end) : List.of();

        return new PageImpl<>(pageContent, pageable, filtered.size());
    }

    @Transactional(readOnly = true)
    public LogEntryResponseDto getEntryById(String id, UserPrincipal currentUser) {
        LogEntry entry = logEntryRepository.findById(id)
                .filter(e -> !Boolean.TRUE.equals(e.getDeleted()))
                .orElseThrow(() -> new IllegalArgumentException("Log entry not found with id: " + id));

        return LogEntryResponseDto.fromEntity(entry);
    }

    public LogEntryResponseDto createEntry(String clientNoteId, LogEntryRequestDto request, UserPrincipal currentUser) {
        ClientNote clientNote = clientNoteRepository.findById(clientNoteId)
                .orElseThrow(() -> new IllegalArgumentException("Client note space not found: " + clientNoteId));

        User author = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new IllegalStateException("User not found: " + currentUser.getId()));

        Tag tag = null;
        if (request.getTagId() != null && !request.getTagId().trim().isEmpty()) {
            tag = tagRepository.findById(request.getTagId()).orElse(null);
        }

        LocalDate interactionDate = request.getInteractionDate() != null ? request.getInteractionDate() : LocalDate.now();

        LogEntry entry = new LogEntry(
                clientNote,
                clientNote.getOrganisation(),
                author,
                request.getInteractionType(),
                interactionDate,
                request.getTextContent()
        );
        entry.setInteractionTime(request.getInteractionTime());
        entry.setTag(tag);
        entry.setPriority(Boolean.TRUE.equals(request.getPriority()));
        entry.setClientTempId(request.getClientTempId());

        LogEntry savedEntry = logEntryRepository.save(entry);

        // Update denormalized note count on clientNote
        clientNote.setEntryCount(clientNote.getEntryCount() + 1);
        clientNoteRepository.save(clientNote);

        // Update denormalized client last interaction and notes count
        Client client = clientNote.getClient();
        if (client != null) {
            client.setNotesCount(client.getNotesCount() + 1);
            client.setLastInteractionAt(LocalDateTime.now());
            clientRepository.save(client);
        }

        return LogEntryResponseDto.fromEntity(savedEntry);
    }

    public LogEntryResponseDto updateEntry(String id, LogEntryRequestDto request, UserPrincipal currentUser) {
        LogEntry entry = logEntryRepository.findById(id)
                .filter(e -> !Boolean.TRUE.equals(e.getDeleted()))
                .orElseThrow(() -> new IllegalArgumentException("Log entry not found: " + id));

        if (request.getInteractionType() != null) entry.setInteractionType(request.getInteractionType());
        if (request.getInteractionDate() != null) entry.setInteractionDate(request.getInteractionDate());
        if (request.getInteractionTime() != null) entry.setInteractionTime(request.getInteractionTime());
        if (request.getTextContent() != null) entry.setTextContent(request.getTextContent());
        if (request.getPriority() != null) entry.setPriority(request.getPriority());

        if (request.getTagId() != null) {
            if (request.getTagId().trim().isEmpty()) {
                entry.setTag(null);
            } else {
                Tag tag = tagRepository.findById(request.getTagId()).orElse(null);
                entry.setTag(tag);
            }
        }

        return LogEntryResponseDto.fromEntity(logEntryRepository.save(entry));
    }

    public LogEntryResponseDto updateTag(String id, String tagId, UserPrincipal currentUser) {
        LogEntry entry = logEntryRepository.findById(id)
                .filter(e -> !Boolean.TRUE.equals(e.getDeleted()))
                .orElseThrow(() -> new IllegalArgumentException("Log entry not found: " + id));

        if (tagId == null || tagId.trim().isEmpty()) {
            entry.setTag(null);
        } else {
            Tag tag = tagRepository.findById(tagId)
                    .orElseThrow(() -> new IllegalArgumentException("Tag not found: " + tagId));
            entry.setTag(tag);
        }

        return LogEntryResponseDto.fromEntity(logEntryRepository.save(entry));
    }

    public LogEntryResponseDto updatePriority(String id, Boolean priority, UserPrincipal currentUser) {
        LogEntry entry = logEntryRepository.findById(id)
                .filter(e -> !Boolean.TRUE.equals(e.getDeleted()))
                .orElseThrow(() -> new IllegalArgumentException("Log entry not found: " + id));

        entry.setPriority(Boolean.TRUE.equals(priority));
        return LogEntryResponseDto.fromEntity(logEntryRepository.save(entry));
    }

    public void deleteEntry(String id, UserPrincipal currentUser) {
        LogEntry entry = logEntryRepository.findById(id)
                .filter(e -> !Boolean.TRUE.equals(e.getDeleted()))
                .orElseThrow(() -> new IllegalArgumentException("Log entry not found: " + id));

        entry.setDeleted(true);
        entry.setDeletedAt(LocalDateTime.now());
        logEntryRepository.save(entry);

        // Decrement counts
        ClientNote clientNote = entry.getClientNote();
        if (clientNote != null && clientNote.getEntryCount() > 0) {
            clientNote.setEntryCount(clientNote.getEntryCount() - 1);
            clientNoteRepository.save(clientNote);

            Client client = clientNote.getClient();
            if (client != null && client.getNotesCount() > 0) {
                client.setNotesCount(client.getNotesCount() - 1);
                clientRepository.save(client);
            }
        }
    }

    public AttachmentResponseDto addLinkAttachment(String entryId, String linkUrl, String linkTitle, UserPrincipal currentUser) {
        LogEntry entry = logEntryRepository.findById(entryId)
                .filter(e -> !Boolean.TRUE.equals(e.getDeleted()))
                .orElseThrow(() -> new IllegalArgumentException("Log entry not found: " + entryId));

        User user = userRepository.findById(currentUser.getId()).orElse(null);

        LogEntryAttachment attachment = new LogEntryAttachment();
        attachment.setLogEntry(entry);
        attachment.setUploadedByUser(user);
        attachment.setAttachmentType("link");
        attachment.setLinkUrl(linkUrl);
        attachment.setLinkTitle(linkTitle != null ? linkTitle : linkUrl);

        LogEntryAttachment saved = attachmentRepository.save(attachment);

        entry.setHasAttachments(true);
        logEntryRepository.save(entry);

        return AttachmentResponseDto.fromEntity(saved);
    }

    public void deleteAttachment(String attachmentId, UserPrincipal currentUser) {
        LogEntryAttachment attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new IllegalArgumentException("Attachment not found: " + attachmentId));

        LogEntry entry = attachment.getLogEntry();
        attachmentRepository.delete(attachment);

        if (entry != null) {
            List<LogEntryAttachment> remaining = attachmentRepository.findByLogEntryId(entry.getId());
            entry.setHasAttachments(!remaining.isEmpty());
            boolean hasVoice = remaining.stream().anyMatch(a -> "voice_note".equalsIgnoreCase(a.getAttachmentType()));
            entry.setHasVoiceNote(hasVoice);
            logEntryRepository.save(entry);
        }
    }
}
