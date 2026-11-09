package com.salesnotes.repository;

import com.salesnotes.entity.LogEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LogEntryRepository extends JpaRepository<LogEntry, String> {
    List<LogEntry> findByClientNoteIdAndDeletedFalseOrderByPriorityDescInteractionDateDescCreatedAtDesc(String clientNoteId);
    Page<LogEntry> findByClientNoteIdAndDeletedFalse(String clientNoteId, Pageable pageable);
    List<LogEntry> findByClientNoteIdAndTagIdAndDeletedFalse(String clientNoteId, String tagId);
    List<LogEntry> findByClientNoteIdAndPriorityTrueAndDeletedFalse(String clientNoteId);
    List<LogEntry> findByClientNoteIdAndHasVoiceNoteTrueAndDeletedFalse(String clientNoteId);
}
