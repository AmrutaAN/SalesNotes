package com.salesnotes.repository;

import com.salesnotes.entity.ClipboardSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClipboardSessionRepository extends JpaRepository<ClipboardSession, String> {
    Optional<ClipboardSession> findByUserIdAndStatus(String userId, String status);
    Optional<ClipboardSession> findByUserId(String userId);
}
