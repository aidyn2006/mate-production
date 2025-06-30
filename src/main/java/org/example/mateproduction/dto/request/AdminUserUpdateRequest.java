// Create a new DTO for the update to be more specific
// org.example.mateproduction.dto.request.AdminUserUpdateRequest.java
package org.example.mateproduction.dto.request;

import lombok.Data;
import org.example.mateproduction.util.Role;

@Data
public class AdminUserUpdateRequest {
    private String name;
    private String surname;
    private String username;
    private String phone;
    private String email;
    private Role role; // Role can be updated
}

