package org.example.mateproduction.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.mateproduction.util.AdType;
import org.example.mateproduction.util.CityNames;
import org.example.mateproduction.util.Status;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
public class AdHouseFilter {
    private String searchQuery;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Integer minRooms;
    private Integer maxRooms;
    private Double minArea;
    private Double maxArea;
    private CityNames city;
    private AdType type;
    private Boolean furnished;
    private Status status;
}
