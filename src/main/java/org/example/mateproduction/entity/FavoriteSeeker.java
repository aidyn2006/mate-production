package org.example.mateproduction.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.mateproduction.entity.contract.FavoriteSeekerId; // Use specific FavoriteSeekerId

import java.time.LocalDateTime; // Use LocalDateTime


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "favorite_seeker_ads") // Add explicit table name
public class FavoriteSeeker {
    @EmbeddedId
    private FavoriteSeekerId id; // Use FavoriteSeekerId

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @MapsId("adSeekerId") // Maps to adSeekerId in FavoriteSeekerId
    @JoinColumn(name = "ad_seeker_id") // Explicit join column name
    private AdSeeker ad; // Renamed from 'ad' for consistency or keep 'adSeeker' if you prefer

    @Column(name = "created_at") // Explicit column name
    private LocalDateTime createdAt; // Use LocalDateTime
}