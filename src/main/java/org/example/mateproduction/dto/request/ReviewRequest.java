package org.example.mateproduction.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewRequest {
    private UUID reviewerId;
    private UUID userId;
    private UUID adId;
    private int rating;
    private String comment;
}