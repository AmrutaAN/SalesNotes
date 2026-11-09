package com.salesnotes.repository;

import com.salesnotes.entity.NoteCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NoteCategoryRepository extends JpaRepository<NoteCategory, Long> {
    Optional<NoteCategory> findByNameIgnoreCase(String name);
}
