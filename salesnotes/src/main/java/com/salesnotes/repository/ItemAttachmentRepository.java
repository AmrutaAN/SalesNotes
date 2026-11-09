package com.salesnotes.repository;

import com.salesnotes.entity.ItemAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemAttachmentRepository extends JpaRepository<ItemAttachment, String> {
    List<ItemAttachment> findByCatalogueItemId(String catalogueItemId);
}
