package org.example.mateproduction.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageRequest {
    private UUID receiverId;

    @NotBlank(message = "Message content cannot be blank.")
    @Size(min = 1, max = 2000, message = "Message must be between 1 and 2000 characters.")
    private String content;
    private String token;
}

