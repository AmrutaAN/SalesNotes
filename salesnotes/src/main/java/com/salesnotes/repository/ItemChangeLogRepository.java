package com.salesnotes.repository;

import com.salesnotes.entity.ItemChangeLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemChangeLogRepository extends JpaRepository<ItemChangeLog, String> {
    List<ItemChangeLog> findByCatalogueItemIdOrderByCreatedAtDesc(String catalogueItemId);
}
