package org.example.mateproduction.entity.contract;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteId implements java.io.Serializable {
    private UUID userId;
    private UUID adId;
}
