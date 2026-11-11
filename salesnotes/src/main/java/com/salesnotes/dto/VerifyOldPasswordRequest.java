package com.salesnotes.dto;

import jakarta.validation.constraints.NotBlank;

public class VerifyOldPasswordRequest {

    @NotBlank(message = "Username or email is required")
    private String usernameOrEmail;

    @NotBlank(message = "Current or old password is required")
    private String oldPassword;

    @NotBlank(message = "New password is required")
    private String newPassword;

    public VerifyOldPasswordRequest() {}

    public VerifyOldPasswordRequest(String usernameOrEmail, String oldPassword, String newPassword) {
        this.usernameOrEmail = usernameOrEmail;
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
    }

    public String getUsernameOrEmail() {
        return usernameOrEmail;
    }

    public void setUsernameOrEmail(String usernameOrEmail) {
        this.usernameOrEmail = usernameOrEmail;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
