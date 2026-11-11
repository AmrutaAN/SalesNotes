package com.salesnotes.repository;

import com.salesnotes.entity.ClipboardEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClipboardEntryRepository extends JpaRepository<ClipboardEntry, String> {

    List<ClipboardEntry> findByClipboardSessionIdOrderByStagedAtDesc(String sessionId);

    Optional<ClipboardEntry> findByClipboardSessionIdAndLogEntryId(String sessionId, String logEntryId);

    boolean existsByClipboardSessionIdAndLogEntryId(String sessionId, String logEntryId);

    void deleteByClipboardSessionId(String sessionId);

    void deleteByClipboardSessionIdAndLogEntryId(String sessionId, String logEntryId);

    @Modifying
    @Query("DELETE FROM ClipboardEntry ce WHERE ce.clipboardSession.id = :sessionId AND ce.id IN :ids")
    void deleteBySessionIdAndIds(@Param("sessionId") String sessionId, @Param("ids") List<String> ids);
}
