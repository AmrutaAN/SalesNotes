package com.salesnotes.repository;

import com.salesnotes.entity.NoteStatus;
import com.salesnotes.entity.Priority;
import com.salesnotes.entity.SalesNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SalesNoteRepository extends JpaRepository<SalesNote, Long> {

    List<SalesNote> findAllByOrderByCreatedAtDesc();

    List<SalesNote> findByCustomerIdOrderByCreatedAtDesc(Long customerId);

    List<SalesNote> findByStatusOrderByCreatedAtDesc(NoteStatus status);

    List<SalesNote> findByPriorityOrderByCreatedAtDesc(Priority priority);

    @Query("SELECT n FROM SalesNote n WHERE " +
           "(:search IS NULL OR LOWER(n.title) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(CAST(n.content AS string)) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(n.customer.name) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(n.customer.company) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(n.tags) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "AND (:status IS NULL OR n.status = :status) " +
           "AND (:priority IS NULL OR n.priority = :priority) " +
           "AND (:customerId IS NULL OR n.customer.id = :customerId) " +
           "ORDER BY n.createdAt DESC")
    List<SalesNote> searchNotes(
            @Param("search") String search,
            @Param("status") NoteStatus status,
            @Param("priority") Priority priority,
            @Param("customerId") Long customerId
    );

    long countByStatus(NoteStatus status);

    long countByPriority(Priority priority);

    @Query("SELECT COALESCE(SUM(n.dealAmount), 0.0) FROM SalesNote n WHERE n.dealAmount IS NOT NULL")
    Double sumTotalDealAmount();
}
