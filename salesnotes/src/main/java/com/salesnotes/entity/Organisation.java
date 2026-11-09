package com.salesnotes.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "organisations")
public class Organisation {

    @Id
    @Column(name = "id", length = 36, nullable = false, updatable = false)
    private String id;

    @NotBlank(message = "Organisation name is required")
    @Column(name = "name", length = 200, nullable = false)
    private String name;

    @NotBlank(message = "Slug is required")
    @Column(name = "slug", length = 80, nullable = false, unique = true)
    private String slug;

    @Column(name = "timezone", length = 60, nullable = false)
    private String timezone = "Asia/Kolkata";

    @Column(name = "currency_code", length = 3, nullable = false)
    private String currencyCode = "INR";

    @Column(name = "is_active", nullable = false)
    private Boolean active = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Organisation() {
        this.id = UUID.randomUUID().toString();
    }

    public Organisation(String name, String slug) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.slug = slug;
        this.active = true;
    }

    @PrePersist
    protected void onCreate() {
        if (this.id == null || this.id.trim().isEmpty()) {
            this.id = UUID.randomUUID().toString();
        }
        LocalDateTime now = LocalDateTime.now();
        if (this.createdAt == null) {
            this.createdAt = now;
        }
        this.updatedAt = now;
        if (this.active == null) {
            this.active = true;
        }
        if (this.timezone == null) {
            this.timezone = "Asia/Kolkata";
        }
        if (this.currencyCode == null) {
            this.currencyCode = "INR";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
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
