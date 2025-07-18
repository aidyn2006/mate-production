package org.example.mateproduction.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.mateproduction.util.Role;
import org.example.mateproduction.util.UserStatus;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
    private UUID id;
    private String name;
    private String surname;
    private String username;
    private String email;
    private String phone;
    private Role role;
    private Boolean isVerified;
    private String avatarUrl;
    private String token;
    private Boolean isDeleted;
    private Date createdAt;
    private UserStatus status;    // New field
    private String banReason;     // New field
}