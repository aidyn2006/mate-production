package org.example.mateproduction.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.mateproduction.util.CityNames;
import org.example.mateproduction.util.Gender;
import org.example.mateproduction.util.RoommatePreference;
import org.example.mateproduction.util.Status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdSeekerFilter {
    private String searchQuery;

    private Integer minAge;
    private Integer maxAge;

    private Gender gender;

    private CityNames city;

    private String desiredLocation;

    private BigDecimal maxBudget;

    private LocalDate earliestMoveInDate;

    private Boolean hasFurnishedPreference;

    private List<RoommatePreference> roommatePreferences;

    private Status status;
}
