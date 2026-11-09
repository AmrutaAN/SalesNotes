package com.salesnotes.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Entity
@Table(name = "clients")
public class Client extends BaseAuditEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organisation_id", nullable = false)
    private Organisation organisation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_rep_id", nullable = false)
    private User assignedRep;

    @NotBlank(message = "Client name is required")
    @Column(name = "name", length = 200, nullable = false)
    private String name;

    @Column(name = "company", length = 200)
    private String company;

    @Column(name = "phone", length = 30)
    private String phone;

    @Column(name = "email", length = 320)
    private String email;

    @Column(name = "address", columnDefinition = "TEXT")
    private String address;

    @Column(name = "industry_tag", length = 80)
    private String industryTag;

    @Column(name = "priority_flag", nullable = false)
    private Boolean priorityFlag = false;

    @Column(name = "last_interaction_at")
    private LocalDateTime lastInteractionAt;

    @Column(name = "notes_count", nullable = false)
    private Integer notesCount = 0;

    @OneToOne(mappedBy = "client", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private ClientNote clientNote;

    public Client() {
        super();
    }

    public Client(Organisation organisation, User assignedRep, String name, String company) {
        super();
        this.organisation = organisation;
        this.assignedRep = assignedRep;
        this.name = name;
        this.company = company;
        this.priorityFlag = false;
        this.notesCount = 0;
    }

    public Organisation getOrganisation() {
        return organisation;
    }

    public void setOrganisation(Organisation organisation) {
        this.organisation = organisation;
    }

    public User getAssignedRep() {
        return assignedRep;
    }

    public void setAssignedRep(User assignedRep) {
        this.assignedRep = assignedRep;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getIndustryTag() {
        return industryTag;
    }

    public void setIndustryTag(String industryTag) {
        this.industryTag = industryTag;
    }

    public Boolean getPriorityFlag() {
        return priorityFlag;
    }

    public void setPriorityFlag(Boolean priorityFlag) {
        this.priorityFlag = priorityFlag;
    }

    public LocalDateTime getLastInteractionAt() {
        return lastInteractionAt;
    }

    public void setLastInteractionAt(LocalDateTime lastInteractionAt) {
        this.lastInteractionAt = lastInteractionAt;
    }

    public Integer getNotesCount() {
        return notesCount;
    }

    public void setNotesCount(Integer notesCount) {
        this.notesCount = notesCount;
    }

    public ClientNote getClientNote() {
        return clientNote;
    }

    public void setClientNote(ClientNote clientNote) {
        this.clientNote = clientNote;
    }
}
