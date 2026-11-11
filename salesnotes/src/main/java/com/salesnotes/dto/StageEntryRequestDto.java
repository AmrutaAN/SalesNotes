package com.salesnotes.dto;

import java.util.List;

public class StageEntryRequestDto {

    private String logEntryId;
    private List<String> logEntryIds;

    public StageEntryRequestDto() {
    }

    public StageEntryRequestDto(String logEntryId) {
        this.logEntryId = logEntryId;
    }

    public StageEntryRequestDto(List<String> logEntryIds) {
        this.logEntryIds = logEntryIds;
    }

    public String getLogEntryId() {
        return logEntryId;
    }

    public void setLogEntryId(String logEntryId) {
        this.logEntryId = logEntryId;
    }

    public List<String> getLogEntryIds() {
        return logEntryIds;
    }

    public void setLogEntryIds(List<String> logEntryIds) {
        this.logEntryIds = logEntryIds;
    }
}
