package com.salesnotes.repository;

import com.salesnotes.entity.CatalogueItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CatalogueItemRepository extends JpaRepository<CatalogueItem, String> {
    List<CatalogueItem> findByOrganisationIdAndActiveTrueAndDeletedFalse(String organisationId);
    Page<CatalogueItem> findByOrganisationIdAndActiveTrueAndDeletedFalse(String organisationId, Pageable pageable);
    Optional<CatalogueItem> findByOrganisationIdAndItemCodeAndDeletedFalse(String organisationId, String itemCode);
    List<CatalogueItem> findByOrganisationIdAndCategoryAndActiveTrueAndDeletedFalse(String organisationId, String category);
    List<CatalogueItem> findByOrganisationIdAndItemNameContainingIgnoreCaseOrItemCodeContainingIgnoreCaseAndActiveTrueAndDeletedFalse(
            String organisationId, String itemName, String itemCode
    );
}
