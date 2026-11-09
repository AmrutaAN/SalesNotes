package com.salesnotes.dto;

import com.salesnotes.entity.User;
import com.salesnotes.entity.UserRole;
import java.time.LocalDateTime;

public class UserResponse {

    private String id;
    private String organisationId;
    private String organisationName;
    private String username;
    private String fullname;
    private String email;
    private String phone;
    private UserRole role;
    private Boolean active;
    private LocalDateTime createdat;
    private LocalDateTime updatedat;

    public UserResponse() {
    }

    public static UserResponse fromEntity(User user) {
        UserResponse res = new UserResponse();
        res.setId(user.getId());
        if (user.getOrganisation() != null) {
            res.setOrganisationId(user.getOrganisation().getId());
            res.setOrganisationName(user.getOrganisation().getName());
        }
        res.setUsername(user.getUsername());
        res.setFullname(user.getFullname());
        res.setEmail(user.getEmail());
        res.setPhone(user.getPhone());
        res.setRole(user.getRole());
        res.setActive(user.getActive());
        res.setCreatedat(user.getCreatedat());
        res.setUpdatedat(user.getUpdatedat());
        return res;
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

    public String getOrganisationName() {
        return organisationName;
    }

    public void setOrganisationName(String organisationName) {
        this.organisationName = organisationName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedat() {
        return createdat;
    }

    public void setCreatedat(LocalDateTime createdat) {
        this.createdat = createdat;
    }

    public LocalDateTime getUpdatedat() {
        return updatedat;
    }

    public void setUpdatedat(LocalDateTime updatedat) {
        this.updatedat = updatedat;
    }
}
