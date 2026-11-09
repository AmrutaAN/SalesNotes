package com.salesnotes.repository;

import com.salesnotes.entity.Client;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClientRepository extends JpaRepository<Client, String> {
    List<Client> findByOrganisationIdAndDeletedFalse(String organisationId);
    Page<Client> findByOrganisationIdAndDeletedFalse(String organisationId, Pageable pageable);
    List<Client> findByAssignedRepIdAndDeletedFalse(String repId);
    Page<Client> findByAssignedRepIdAndDeletedFalse(String repId, Pageable pageable);
    List<Client> findByOrganisationIdAndNameContainingIgnoreCaseAndDeletedFalse(String organisationId, String name);
}
