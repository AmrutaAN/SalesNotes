package com.salesnotes.repository;

import com.salesnotes.entity.ClientNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientNoteRepository extends JpaRepository<ClientNote, String> {
    Optional<ClientNote> findByClientId(String clientId);
    Optional<ClientNote> findByClientIdAndOrganisationId(String clientId, String organisationId);
}
