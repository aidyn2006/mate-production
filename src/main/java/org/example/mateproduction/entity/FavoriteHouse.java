package org.example.mateproduction.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.mateproduction.entity.contract.FavoriteHouseId; // Use specific FavoriteHouseId

import java.time.LocalDateTime; // Use LocalDateTime

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "favorite_house_ads") // Good, explicit table name
public class FavoriteHouse {

    @EmbeddedId
    private FavoriteHouseId id; // Use FavoriteHouseId

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @MapsId("adHouseId") // Maps to adHouseId in FavoriteHouseId
    @JoinColumn(name = "ad_house_id") // Explicit join column name
    private AdHouse ad; // Renamed from 'ad' for consistency or keep 'adHouse' if you prefer

    @Column(name = "created_at") // Explicit column name
    private LocalDateTime createdAt; // Use LocalDateTime
}