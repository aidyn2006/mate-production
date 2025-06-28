package org.example.mateproduction.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.mateproduction.entity.base.Ad;
import org.example.mateproduction.entity.base.BaseEntity;
import org.example.mateproduction.service.Moderatable;
import org.example.mateproduction.util.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdSeeker extends Ad implements Moderatable {

    @NotNull(message = "Age is required")
    @Min(value = 18, message = "Age must be at least 18")
    @Max(value = 100, message = "Age must be 100 or less") // <-- MODIFIED
    private Integer age;

    @NotNull(message = "Gender is required")
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @NotBlank(message = "Seeker description cannot be empty")
    @Size(min = 50, max = 2000, message = "Description must be between 50 and 2000 characters")
    @Column(length = 2000)
    private String seekerDescription;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull(message = "City is required")
    @Enumerated(EnumType.STRING)
    private CityNames city;

    @Size(max = 255, message = "Desired location can be up to 255 characters")
    private String desiredLocation;

    @NotNull(message = "Maximum budget is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Budget must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "Budget value is too large") // <-- MODIFIED
    private BigDecimal maxBudget;

    @NotNull(message = "Move-in date is required")
    @FutureOrPresent(message = "Move-in date must be in the present or future")
    private LocalDate moveInDate;

    @NotNull
    private Boolean hasFurnishedPreference;

    @ElementCollection(targetClass = RoommatePreference.class, fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "seeker_roommate_preferences", joinColumns = @JoinColumn(name = "seeker_id"))
    @Column(name = "preference")
    private List<RoommatePreference> roommatePreferences;

    @NotNull(message = "Preferred roommate gender is required")
    @Enumerated(EnumType.STRING)
    private Gender preferredRoommateGender;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Status status;

    @Builder.Default
    private Integer views = 0;

    @NotBlank(message = "Contact phone number is required")
    @Pattern(regexp = "^\\+?[0-9.\\s()-]{7,20}$", message = "Invalid phone number format")
    @Size(max = 25, message = "Phone number is too long")
    private String contactPhoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Type typeOfAd;

    @Column(name = "moderation_comment", nullable = true)
    private String moderationComment;
}