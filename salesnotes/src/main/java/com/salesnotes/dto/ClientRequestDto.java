package com.salesnotes.dto;

import jakarta.validation.constraints.NotBlank;

public class ClientRequestDto {

    @NotBlank(message = "Client name is required")
    private String name;

    private String company;
    private String phone;
    private String email;
    private String address;
    private String industryTag;
    private Boolean priorityFlag = false;
    private String assignedRepId;

    public ClientRequestDto() {
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

    public String getAssignedRepId() {
        return assignedRepId;
    }

    public void setAssignedRepId(String assignedRepId) {
        this.assignedRepId = assignedRepId;
    }
}
