package org.example.mateproduction.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.mateproduction.entity.base.BaseEntity;
import org.example.mateproduction.util.AdType;
import org.example.mateproduction.util.CityNames;
import org.example.mateproduction.util.Status;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "ad")
public class AdHouse extends BaseEntity {

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private BigDecimal price;
    private String address;


    private CityNames city;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private AdType type;

    @Enumerated(EnumType.STRING)
    private Status status;

    @ElementCollection
    @CollectionTable(name = "ad_images", joinColumns = @JoinColumn(name = "ad_id"))
    @Column(name = "image_url")
    private List<String> images;

    private Integer numberOfRooms;
    private Double area;
    private Integer floor;
    private Boolean furnished;

    private String contactPhoneNumber;

    private Integer views;

}
