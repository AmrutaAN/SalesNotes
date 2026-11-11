package com.salesnotes.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum UserRole {
    SUPERUSER("SuperUser"),
    SYSADMIN("Sysadmin"),
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
        String clean = text.trim();
        for (UserRole r : UserRole.values()) {
            if (r.name().equalsIgnoreCase(clean) || r.value.equalsIgnoreCase(clean)) {
                return r;
            }
        }
        if ("superuser".equalsIgnoreCase(clean)) return SUPERUSER;
        if ("sysadmin".equalsIgnoreCase(clean)) return SYSADMIN;
        if ("companyadmin".equalsIgnoreCase(clean) || "admin".equalsIgnoreCase(clean)) return COMPANYADMIN;
        if ("companyemployee".equalsIgnoreCase(clean) || "employee".equalsIgnoreCase(clean) || "rep".equalsIgnoreCase(clean)) return COMPANYEMPLOYEE;

        return COMPANYEMPLOYEE;
    }
}
