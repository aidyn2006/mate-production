package org.example.mateproduction.entity.contract;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteId implements java.io.Serializable {
    private UUID userId;
    private UUID adId;
}