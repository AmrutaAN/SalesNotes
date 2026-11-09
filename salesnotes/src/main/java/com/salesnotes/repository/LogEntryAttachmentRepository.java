package com.salesnotes.repository;

import com.salesnotes.entity.LogEntryAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LogEntryAttachmentRepository extends JpaRepository<LogEntryAttachment, String> {
    List<LogEntryAttachment> findByLogEntryId(String logEntryId);
    List<LogEntryAttachment> findByLogEntryIdAndAttachmentType(String logEntryId, String attachmentType);
}
