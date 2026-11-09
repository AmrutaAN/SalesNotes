package com.salesnotes.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum UserRole {
    SYSADMIN("SYSADMIN"),
    COMPANYADMIN("companyadmin"),
    COMPANYEMPLOYEE("companyemployee");

    private final String value;

    UserRole(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static UserRole fromString(String text) {
        if (text == null) {
            return null;
        }
        for (UserRole r : UserRole.values()) {
            if (r.name().equalsIgnoreCase(text) || r.value.equalsIgnoreCase(text)) {
                return r;
            }
        }
        throw new IllegalArgumentException("Unknown role: " + text + ". Allowed roles: SYSADMIN, companyadmin, companyemployee");
    }
}
