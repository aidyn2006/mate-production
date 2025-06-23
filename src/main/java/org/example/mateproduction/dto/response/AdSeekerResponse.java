package org.example.mateproduction.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.mateproduction.util.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdSeekerResponse {

    private UUID id;

    private Integer age;

    private Gender gender;

    private UserResponse user;

    private String seekerDescription;

    private CityNames city;

    private String desiredLocation;

    private BigDecimal maxBudget;

    private LocalDate moveInDate;

    private Boolean hasFurnishedPreference;

    private List<RoommatePreference> roommatePreferences;

    private Gender preferredRoommateGender;

    private Status status;

    private Integer views;
    private Type typeOfAd;

    private String contactPhoneNumber;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Almaty")
    private Date createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Almaty")
    private Date updatedAt;

    private String moderationComment;

}
