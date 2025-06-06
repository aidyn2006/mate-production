package org.example.mateproduction.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.mateproduction.entity.City;
import org.example.mateproduction.entity.User;
import org.example.mateproduction.util.AdType;
import org.example.mateproduction.util.Status;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdRequest {
    private String title;
    private String description;
    private BigDecimal price;
    private String address;
    private City city;
    private User user;
    private AdType type;
    private Status status;

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

    private List<AdImageRequest> images;

}