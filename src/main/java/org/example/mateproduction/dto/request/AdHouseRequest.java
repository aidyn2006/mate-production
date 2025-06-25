package org.example.mateproduction.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.mateproduction.util.AdType;
import org.example.mateproduction.util.CityNames;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdHouseRequest {
    private String title;
    private String description;
    private BigDecimal price;
    private String address;
    private CityNames city;
    private AdType type;
    private String contactPhoneNumber;

    private Integer numberOfRooms;
    private Double area;
    private Integer floor;
    private Boolean furnished;

    // Corrected to follow Java naming conventions (camelCase)
    private String mainImageUrl;

    private List<MultipartFile> images;
}