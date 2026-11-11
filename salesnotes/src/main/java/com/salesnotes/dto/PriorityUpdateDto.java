package com.salesnotes.dto;

public class PriorityUpdateDto {
    private Boolean priority = false;

    public PriorityUpdateDto() {
    }

    public PriorityUpdateDto(Boolean priority) {
        this.priority = priority;
    }

    public Boolean getPriority() {
        return priority;
    }

    public void setPriority(Boolean priority) {
        this.priority = priority;
    }
}
