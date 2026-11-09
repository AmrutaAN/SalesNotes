package com.salesnotes.dto;

import com.salesnotes.entity.NoteCategory;
import com.salesnotes.entity.NoteStatus;
import com.salesnotes.entity.Priority;
import com.salesnotes.entity.SalesNote;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class SalesNoteResponse {

    private Long id;
    private String title;
    private String content;
    private CustomerDto customer;
    private NoteCategory category;
    private Double dealAmount;
    private Priority priority;
    private NoteStatus status;
    private LocalDate followUpDate;
    private String tags;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public SalesNoteResponse() {
    }

    public static SalesNoteResponse fromEntity(SalesNote note) {
        SalesNoteResponse res = new SalesNoteResponse();
        res.setId(note.getId());
        res.setTitle(note.getTitle());
        res.setContent(note.getContent());
        if (note.getCustomer() != null) {
            res.setCustomer(new CustomerDto(
                    note.getCustomer().getId(),
                    note.getCustomer().getName(),
                    note.getCustomer().getCompany(),
                    note.getCustomer().getEmail(),
                    note.getCustomer().getPhone(),
                    note.getCustomer().getIndustry()
            ));
        }
        res.setCategory(note.getCategory());
        res.setDealAmount(note.getDealAmount());
        res.setPriority(note.getPriority());
        res.setStatus(note.getStatus());
        res.setFollowUpDate(note.getFollowUpDate());
        res.setTags(note.getTags());
        res.setCreatedAt(note.getCreatedAt());
        res.setUpdatedAt(note.getUpdatedAt());
        return res;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public CustomerDto getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerDto customer) {
        this.customer = customer;
    }

    public NoteCategory getCategory() {
        return category;
    }

    public void setCategory(NoteCategory category) {
        this.category = category;
    }

    public Double getDealAmount() {
        return dealAmount;
    }

    public void setDealAmount(Double dealAmount) {
        this.dealAmount = dealAmount;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public NoteStatus getStatus() {
        return status;
    }

    public void setStatus(NoteStatus status) {
        this.status = status;
    }

    public LocalDate getFollowUpDate() {
        return followUpDate;
    }

    public void setFollowUpDate(LocalDate followUpDate) {
        this.followUpDate = followUpDate;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
