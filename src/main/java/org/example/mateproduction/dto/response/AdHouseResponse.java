package org.example.mateproduction.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.mateproduction.util.AdType;
import org.example.mateproduction.util.CityNames;
import org.example.mateproduction.util.Status;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdHouseResponse {
    private UUID id;
    private String title;
    private String description;
    private BigDecimal price;
    private String address;

    private CityNames city;
    private UserResponse user;

    private AdType type;
    private Status status;

    private List<String> images;

    private Integer numberOfRooms;
    private Double area;
    private Integer floor;
    private Boolean furnished;

    private String contactPhoneNumber;

    private Integer views;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Almaty")
    private Date createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Almaty")
    private Date updatedAt;



}
