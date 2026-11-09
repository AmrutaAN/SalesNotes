package com.salesnotes.repository;

import com.salesnotes.entity.ExportRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExportRecordRepository extends JpaRepository<ExportRecord, String> {
    List<ExportRecord> findByClipboardSessionId(String sessionId);
    List<ExportRecord> findByExportedByIdOrderByExportedAtDesc(String userId);
    List<ExportRecord> findByOrganisationIdAndTargetModule(String organisationId, String targetModule);
}
