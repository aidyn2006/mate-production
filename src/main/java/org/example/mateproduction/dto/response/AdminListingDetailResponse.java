package org.example.mateproduction.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.example.mateproduction.util.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
@SuperBuilder
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // Exclude null fields from JSON output
public class AdminListingDetailResponse {
    // Common Fields
    private UUID id;
    private UserResponse user;
    private Status status;
    private String moderationComment;
    private boolean featured;
    private Integer views;
    private String contactPhoneNumber;
    private Type typeOfAd;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Aqtau")
    private Date createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Aqtau")
    private Date updatedAt;

    // AdHouse Specific
    private String title;
    private String description;
    private BigDecimal price;
    private String address;
    private CityNames city;
    private AdType type;
    private List<String> images;
    private Integer numberOfRooms;
    private Double area;
    private Integer floor;
    private Boolean furnished;
    private String mainImageUrl;

    // AdSeeker Specific
    private Integer age;
    private Gender gender;
    private String seekerDescription;
    private String desiredLocation;
    private BigDecimal maxBudget;
    private LocalDate moveInDate;
    private Boolean hasFurnishedPreference;
    private List<RoommatePreference> roommatePreferences;
    private Gender preferredRoommateGender;
}