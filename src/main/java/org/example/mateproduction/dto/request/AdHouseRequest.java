package org.example.mateproduction.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.mateproduction.util.AdType;
import org.example.mateproduction.util.CityNames;
import org.example.mateproduction.util.Status;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

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
    private Status status;
    private String contactPhoneNumber;


    private Integer numberOfRooms;
    private Double area;
    private Integer floor;
    private Boolean furnished;

    private List<MultipartFile> images;

}