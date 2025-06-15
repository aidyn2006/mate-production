package org.example.mateproduction.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChangePasswordRequest {
    private String oldPassword;
    private String newPassword;
    private String confirmPassword;
}
