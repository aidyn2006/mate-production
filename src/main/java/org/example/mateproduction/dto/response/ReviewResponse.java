package org.example.mateproduction.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.mateproduction.entity.Ad;
import org.example.mateproduction.entity.User;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewResponse {
    private UUID id;
    private UserResponse reviewer;
    private UserResponse user;
    private AdResponse ad;
    private int rating;
    private String comment;
}