package org.example.mateproduction.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.mateproduction.util.AdType;
import org.example.mateproduction.util.Status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdResponse {
    private UUID id;
    private String title;
    private String description;
    private BigDecimal price;
    private String address;

    private CityResponse city;
    private UserResponse user;

    private AdType type;
    private Status status;
    private List<AdImageResponse> imageUrls;

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

    private Date createdAt;
    private Date updatedAt;
}
