package com.salesnotes.service;

import com.salesnotes.dto.SalesNoteRequest;
import com.salesnotes.dto.SalesNoteResponse;
import com.salesnotes.entity.Customer;
import com.salesnotes.entity.NoteCategory;
import com.salesnotes.entity.NoteStatus;
import com.salesnotes.entity.Priority;
import com.salesnotes.entity.SalesNote;
import com.salesnotes.repository.CustomerRepository;
import com.salesnotes.repository.NoteCategoryRepository;
import com.salesnotes.repository.SalesNoteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class SalesNoteService {

    private final SalesNoteRepository salesNoteRepository;
    private final CustomerRepository customerRepository;
    private final NoteCategoryRepository noteCategoryRepository;

    public SalesNoteService(SalesNoteRepository salesNoteRepository,
                            CustomerRepository customerRepository,
                            NoteCategoryRepository noteCategoryRepository) {
        this.salesNoteRepository = salesNoteRepository;
        this.customerRepository = customerRepository;
        this.noteCategoryRepository = noteCategoryRepository;
    }

    public List<SalesNoteResponse> getNotes(String search, NoteStatus status, Priority priority, Long customerId) {
        List<SalesNote> notes;
        if ((search != null && !search.trim().isEmpty()) || status != null || priority != null || customerId != null) {
            String searchTerm = (search != null && !search.trim().isEmpty()) ? search.trim() : null;
            notes = salesNoteRepository.searchNotes(searchTerm, status, priority, customerId);
        } else {
            notes = salesNoteRepository.findAllByOrderByCreatedAtDesc();
        }
        return notes.stream().map(SalesNoteResponse::fromEntity).collect(Collectors.toList());
    }

    public Optional<SalesNoteResponse> getNoteById(Long id) {
        return salesNoteRepository.findById(id).map(SalesNoteResponse::fromEntity);
    }

    public SalesNoteResponse createNote(SalesNoteRequest request) {
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new IllegalArgumentException("Customer not found with id: " + request.getCustomerId()));

        SalesNote note = new SalesNote();
        note.setTitle(request.getTitle());
        note.setContent(request.getContent());
        note.setCustomer(customer);
        if (request.getCategoryId() != null) {
            noteCategoryRepository.findById(request.getCategoryId()).ifPresent(note::setCategory);
        }
        note.setDealAmount(request.getDealAmount());
        note.setPriority(request.getPriority() != null ? request.getPriority() : Priority.MEDIUM);
        note.setStatus(request.getStatus() != null ? request.getStatus() : NoteStatus.OPEN);
        note.setFollowUpDate(request.getFollowUpDate());
        note.setTags(request.getTags());

        SalesNote saved = salesNoteRepository.save(note);
        return SalesNoteResponse.fromEntity(saved);
    }

    public Optional<SalesNoteResponse> updateNote(Long id, SalesNoteRequest request) {
        return salesNoteRepository.findById(id).map(existing -> {
            existing.setTitle(request.getTitle());
            existing.setContent(request.getContent());

            if (request.getCustomerId() != null &&
                    (existing.getCustomer() == null || !request.getCustomerId().equals(existing.getCustomer().getId()))) {
                Customer customer = customerRepository.findById(request.getCustomerId())
                        .orElseThrow(() -> new IllegalArgumentException("Customer not found with id: " + request.getCustomerId()));
                existing.setCustomer(customer);
            }

            if (request.getCategoryId() != null) {
                noteCategoryRepository.findById(request.getCategoryId()).ifPresent(existing::setCategory);
            } else {
                existing.setCategory(null);
            }

            existing.setDealAmount(request.getDealAmount());
            if (request.getPriority() != null) {
                existing.setPriority(request.getPriority());
            }
            if (request.getStatus() != null) {
                existing.setStatus(request.getStatus());
            }
            existing.setFollowUpDate(request.getFollowUpDate());
            existing.setTags(request.getTags());

            return SalesNoteResponse.fromEntity(salesNoteRepository.save(existing));
        });
    }

    public Optional<SalesNoteResponse> updateStatus(Long id, NoteStatus status) {
        return salesNoteRepository.findById(id).map(existing -> {
            existing.setStatus(status);
            return SalesNoteResponse.fromEntity(salesNoteRepository.save(existing));
        });
    }

    public boolean deleteNote(Long id) {
        if (salesNoteRepository.existsById(id)) {
            salesNoteRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
