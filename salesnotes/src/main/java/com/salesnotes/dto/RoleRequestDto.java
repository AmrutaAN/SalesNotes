package com.salesnotes.dto;

import jakarta.validation.constraints.NotBlank;

public class RoleRequestDto {

    @NotBlank(message = "Role code is required (e.g. RegionalManager, Auditor, SuperUser)")
    private String code;

    @NotBlank(message = "Role display name is required")
    private String name;

    private String description;

    public RoleRequestDto() {
    }

    public RoleRequestDto(String code, String name, String description) {
        this.code = code;
        this.name = name;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
