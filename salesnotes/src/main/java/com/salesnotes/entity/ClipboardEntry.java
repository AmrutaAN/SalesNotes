package com.salesnotes.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "clipboard_entries")
public class ClipboardEntry {

    @Id
    @Column(name = "id", length = 36, nullable = false, updatable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clipboard_session_id", nullable = false)
    private ClipboardSession clipboardSession;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "log_entry_id", nullable = false)
    private LogEntry logEntry;

    @Column(name = "staged_at", nullable = false, updatable = false)
    private LocalDateTime stagedAt;

    public ClipboardEntry() {
        this.id = UUID.randomUUID().toString();
    }

    public ClipboardEntry(ClipboardSession clipboardSession, LogEntry logEntry) {
        this.id = UUID.randomUUID().toString();
        this.clipboardSession = clipboardSession;
        this.logEntry = logEntry;
    }

    @PrePersist
    protected void onCreate() {
        if (this.id == null || this.id.trim().isEmpty()) {
            this.id = UUID.randomUUID().toString();
        }
        if (this.stagedAt == null) {
            this.stagedAt = LocalDateTime.now();
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ClipboardSession getClipboardSession() {
        return clipboardSession;
    }

    public void setClipboardSession(ClipboardSession clipboardSession) {
        this.clipboardSession = clipboardSession;
    }

    public LogEntry getLogEntry() {
        return logEntry;
    }

    public void setLogEntry(LogEntry logEntry) {
        this.logEntry = logEntry;
    }

    public LocalDateTime getStagedAt() {
        return stagedAt;
    }

    public void setStagedAt(LocalDateTime stagedAt) {
        this.stagedAt = stagedAt;
    }
}
