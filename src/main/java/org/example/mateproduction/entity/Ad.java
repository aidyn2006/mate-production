package org.example.mateproduction.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.mateproduction.entity.base.BaseEntity;
import org.example.mateproduction.util.AdType;
import org.example.mateproduction.util.Status;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "ad")
public class Ad extends BaseEntity {

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private BigDecimal price;
    private String address;

    @ManyToOne
    private City city;

    @ManyToOne
    private User user;

    @Enumerated(EnumType.STRING)
    private AdType type;

    @Enumerated(EnumType.STRING)
    private Status status;

    @OneToMany(mappedBy = "ad", cascade = CascadeType.ALL)
    private List<AdImage> images;

    private Integer numberOfRooms;
    private Double area;
    private Integer floor;
    private Boolean furnished;

    private String roommatePreferences;
    private Boolean isSharedRoom;

    private String seekerDescription;
    private String desiredLocation;
    private String desiredRoommatePreferences;
    private BigDecimal maxBudget;
}
