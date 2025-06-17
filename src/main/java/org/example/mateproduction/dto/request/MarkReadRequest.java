package org.example.mateproduction.dto.request;

import lombok.Data;

import java.util.UUID;

@Data
public class MarkReadRequest {
    private UUID senderId;
    private UUID receiverId;
}
