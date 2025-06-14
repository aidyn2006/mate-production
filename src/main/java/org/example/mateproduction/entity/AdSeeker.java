package org.example.mateproduction.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.mateproduction.entity.base.BaseEntity;
import org.example.mateproduction.util.CityNames;
import org.example.mateproduction.util.Gender;
import org.example.mateproduction.util.RoommatePreference;
import org.example.mateproduction.util.Status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "ad_seeker")
public class AdSeeker extends BaseEntity {

    private Integer age;

    @Enumerated(EnumType.STRING)
    private Gender gender;
    private String seekerDescription;


    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private CityNames city;

    private String desiredLocation;
    private BigDecimal maxBudget;
    private LocalDate moveInDate;

    private Boolean hasFurnishedPreference;


    @ElementCollection(targetClass = RoommatePreference.class)
    @Enumerated(EnumType.STRING)
    private List<RoommatePreference> roommatePreferences;

    private Gender preferredRoommateGender;

    @Enumerated(EnumType.STRING)
    private Status status;

    private Integer views;
    private String contactPhoneNumber;


}
