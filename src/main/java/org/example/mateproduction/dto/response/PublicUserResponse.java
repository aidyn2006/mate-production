package org.example.mateproduction.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Data
@Builder
public class PublicUserResponse {
    private UUID id;
    private String name;
    private String surname;
    private String username;
    private String avatarUrl;
    private Date createdAt;
}