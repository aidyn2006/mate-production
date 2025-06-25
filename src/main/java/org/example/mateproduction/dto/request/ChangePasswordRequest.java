package org.example.mateproduction.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class ChangePasswordRequest {
    @NotEmpty(message = "Old password cannot be empty")
    private String oldPassword;

    @NotEmpty(message = "New password cannot be empty")
    private String newPassword;
}