package com.salesnotes.dto;

import java.time.LocalDateTime;

public class ClientSummaryDto {

    private String clientId;
    private String clientName;
    private String company;
    private String clientNoteId;
    private LocalDateTime lastInteractionAt;
    private Integer totalEntriesCount;
    private long openOrdersCount;
    private long openComplaintsCount;
    private long openFollowupsCount;
    private Boolean priorityFlag;

    public ClientSummaryDto() {
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getClientNoteId() {
        return clientNoteId;
    }

    public void setClientNoteId(String clientNoteId) {
        this.clientNoteId = clientNoteId;
    }

    public LocalDateTime getLastInteractionAt() {
        return lastInteractionAt;
    }

    public void setLastInteractionAt(LocalDateTime lastInteractionAt) {
        this.lastInteractionAt = lastInteractionAt;
    }

    public Integer getTotalEntriesCount() {
        return totalEntriesCount;
    }

    public void setTotalEntriesCount(Integer totalEntriesCount) {
        this.totalEntriesCount = totalEntriesCount;
    }

    public long getOpenOrdersCount() {
        return openOrdersCount;
    }

    public void setOpenOrdersCount(long openOrdersCount) {
        this.openOrdersCount = openOrdersCount;
    }

    public long getOpenComplaintsCount() {
        return openComplaintsCount;
    }

    public void setOpenComplaintsCount(long openComplaintsCount) {
        this.openComplaintsCount = openComplaintsCount;
    }

    public long getOpenFollowupsCount() {
        return openFollowupsCount;
    }

    public void setOpenFollowupsCount(long openFollowupsCount) {
        this.openFollowupsCount = openFollowupsCount;
    }

    public Boolean getPriorityFlag() {
        return priorityFlag;
    }

    public void setPriorityFlag(Boolean priorityFlag) {
        this.priorityFlag = priorityFlag;
    }
}
