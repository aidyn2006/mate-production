package org.example.mateproduction.entity.contract;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class FavoriteHouseId implements Serializable {
    @Column(name = "user_id")
    private UUID userId;
    @Column(name = "ad_house_id") // Specific for AdHouse
    private UUID adHouseId;
}