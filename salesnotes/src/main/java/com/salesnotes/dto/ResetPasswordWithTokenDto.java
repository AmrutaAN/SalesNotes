package com.salesnotes.dto;

import jakarta.validation.constraints.NotBlank;

public class ResetPasswordWithTokenDto {

    @NotBlank(message = "Reset token is required")
    private String token;

    private String usernameOrEmail;

    @NotBlank(message = "New password is required")
    private String newPassword;

    public ResetPasswordWithTokenDto() {}

    public ResetPasswordWithTokenDto(String token, String usernameOrEmail, String newPassword) {
        this.token = token;
        this.usernameOrEmail = usernameOrEmail;
        this.newPassword = newPassword;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsernameOrEmail() {
        return usernameOrEmail;
    }

    public void setUsernameOrEmail(String usernameOrEmail) {
        this.usernameOrEmail = usernameOrEmail;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
