package org.example.mateproduction.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
@Builder
public class Favorite {

    @Id
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Id
    @ManyToOne
    @JoinColumn(name = "ad_id", referencedColumnName = "id")
    private AdHouse ad;

    private Date createdAt;
}
