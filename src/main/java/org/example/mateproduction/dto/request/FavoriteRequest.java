package org.example.mateproduction.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.mateproduction.util.Type;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FavoriteRequest {
    private UUID adId; // The ID of the ad (either house or seeker)
    private Type type; // To specify if it's a HOUSE or SEEKER ad
}