package org.example.mateproduction.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // This is key: it won't include null fields in the JSON response
public class LoginResponse {
    private boolean twoFactorEnabled;
    private UserResponse user; // Will be null if 2FA is enabled, present if not.
}