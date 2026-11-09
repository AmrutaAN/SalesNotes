package com.salesnotes.repository;

import com.salesnotes.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, String> {
    List<Tag> findByOrganisationIdAndIsActiveTrueOrderBySortOrderAsc(String organisationId);
    Optional<Tag> findByOrganisationIdAndCode(String organisationId, String code);
    boolean existsByOrganisationIdAndCode(String organisationId, String code);
}
