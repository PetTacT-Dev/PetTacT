package com.pettact.api.user.dto;

import lombok.Data;

@Data
public class PasswordResetDTO {
    private String userEmail;
    private String newPassword;
}
