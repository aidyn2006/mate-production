package org.example.mateproduction.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.mateproduction.entity.contract.FavoriteId;

import java.util.Date;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(FavoriteId.class)
@Table(name = "favorite")
public class Favorite {

    @Id
    private UUID userId;

    @Id
    private UUID adId;

    private Date createdAt;
}