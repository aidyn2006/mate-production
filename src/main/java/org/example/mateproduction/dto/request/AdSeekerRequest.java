package org.example.mateproduction.dto.request;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.mateproduction.entity.User;
import org.example.mateproduction.util.CityNames;
import org.example.mateproduction.util.Gender;
import org.example.mateproduction.util.RoommatePreference;
import org.example.mateproduction.util.Status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdSeekerRequest {

    private Integer age;
    private Gender gender;
    private String seekerDescription;
    private CityNames city;
    private String desiredLocation;
    private BigDecimal maxBudget;
    private LocalDate moveInDate;
    private Boolean hasFurnishedPreference;
    private List<RoommatePreference> roommatePreferences;
    private Gender preferredRoommateGender;
    private String contactPhoneNumber;
}
