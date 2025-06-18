package org.example.mateproduction.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class ResetPasswordRequest {
    @NotEmpty(message = "Token cannot be empty.")
    private String token;

    @NotEmpty(message = "Password cannot be empty.")
    private String newPassword;

    @NotEmpty(message = "Confirm password cannot be empty.")
    private String confirmPassword;
}