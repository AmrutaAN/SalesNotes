package com.salesnotes.repository;

import com.salesnotes.entity.VoiceTranscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VoiceTranscriptionRepository extends JpaRepository<VoiceTranscription, String> {
    Optional<VoiceTranscription> findByLogEntryAttachmentId(String attachmentId);
    Optional<VoiceTranscription> findByItemAttachmentId(String itemAttachmentId);
    List<VoiceTranscription> findByStatusOrderByCreatedAtAsc(String status);
    List<VoiceTranscription> findByRequestedByIdOrderByCreatedAtDesc(String userId);
}
