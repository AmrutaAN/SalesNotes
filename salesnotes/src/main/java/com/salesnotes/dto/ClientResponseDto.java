package com.salesnotes.dto;

import com.salesnotes.entity.Client;
import java.time.LocalDateTime;

public class ClientResponseDto {

    private String id;
    private String organisationId;
    private String assignedRepId;
    private String assignedRepName;
    private String name;
    private String company;
    private String phone;
    private String email;
    private String address;
    private String industryTag;
    private Boolean priorityFlag;
    private LocalDateTime lastInteractionAt;
    private Integer notesCount;
    private String clientNoteId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ClientResponseDto() {
    }

    public static ClientResponseDto fromEntity(Client client) {
        ClientResponseDto dto = new ClientResponseDto();
        dto.setId(client.getId());
        if (client.getOrganisation() != null) {
            dto.setOrganisationId(client.getOrganisation().getId());
        }
        if (client.getAssignedRep() != null) {
            dto.setAssignedRepId(client.getAssignedRep().getId());
            dto.setAssignedRepName(client.getAssignedRep().getFullname());
        }
        dto.setName(client.getName());
        dto.setCompany(client.getCompany());
        dto.setPhone(client.getPhone());
        dto.setEmail(client.getEmail());
        dto.setAddress(client.getAddress());
        dto.setIndustryTag(client.getIndustryTag());
        dto.setPriorityFlag(client.getPriorityFlag());
        dto.setLastInteractionAt(client.getLastInteractionAt());
        dto.setNotesCount(client.getNotesCount());
        if (client.getClientNote() != null) {
            dto.setClientNoteId(client.getClientNote().getId());
        }
        dto.setCreatedAt(client.getCreatedAt());
        dto.setUpdatedAt(client.getUpdatedAt());
        return dto;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrganisationId() {
        return organisationId;
    }

    public void setOrganisationId(String organisationId) {
        this.organisationId = organisationId;
    }

    public String getAssignedRepId() {
        return assignedRepId;
    }

    public void setAssignedRepId(String assignedRepId) {
        this.assignedRepId = assignedRepId;
    }

    public String getAssignedRepName() {
        return assignedRepName;
    }

    public void setAssignedRepName(String assignedRepName) {
        this.assignedRepName = assignedRepName;
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

    public String getClientNoteId() {
        return clientNoteId;
    }

    public void setClientNoteId(String clientNoteId) {
        this.clientNoteId = clientNoteId;
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
