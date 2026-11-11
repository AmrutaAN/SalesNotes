package com.salesnotes.dto;

public class TagUpdateDto {
    private String tagId;

    public TagUpdateDto() {
    }

    public TagUpdateDto(String tagId) {
        this.tagId = tagId;
    }

    public String getTagId() {
        return tagId;
    }

    public void setTagId(String tagId) {
        this.tagId = tagId;
    }
}
