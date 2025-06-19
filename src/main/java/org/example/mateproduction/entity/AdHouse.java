package org.example.mateproduction.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.mateproduction.entity.base.BaseEntity;
import org.example.mateproduction.util.AdType;
import org.example.mateproduction.util.CityNames;
import org.example.mateproduction.util.Status;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdHouse extends BaseEntity {

    @NotBlank(message = "Title is required and cannot be empty.")
    @Size(min = 10, max = 100, message = "Title must be between 10 and 100 characters.")
    @Column(length = 100)
    private String title;

    @NotBlank(message = "Description is required and cannot be empty.")
    @Size(min = 50, max = 5000, message = "Description must be between 50 and 5000 characters.")
    @Column(columnDefinition = "TEXT") // TEXT type for long descriptions
    private String description;

    @NotNull(message = "Price is required.")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be a positive value.")
    private BigDecimal price;

    @NotBlank(message = "Address is required.")
    @Size(max = 255, message = "Address cannot be longer than 255 characters.")
    @Column(length = 255)
    private String address;

    @NotNull(message = "City is required.")
    @Enumerated(EnumType.STRING)
    private CityNames city;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull(message = "Listing type is required.")
    @Enumerated(EnumType.STRING)
    private AdType type;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Status status;

    @NotEmpty(message = "At least one image is required.")
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "ad_house_images", joinColumns = @JoinColumn(name = "ad_house_id"))
    @Column(name = "image_url", nullable = false)
    private List<String> images;

    @NotNull(message = "Number of rooms is required.")
    @Min(value = 1, message = "Number of rooms must be at least 1.")
    private Integer numberOfRooms;

    @NotNull(message = "Area is required.")
    @Positive(message = "Area must be a positive number.")
    private Double area;

    @NotNull(message = "Floor is required.")
    @Min(value = 0, message = "Floor cannot be a negative number.")
    private Integer floor;

    @NotNull(message = "You must specify if the property is furnished.")
    private Boolean furnished;

    @NotBlank(message = "Contact phone number is required.")
    @Pattern(regexp = "^\\+?[0-9.\\s()-]{7,20}$", message = "Invalid phone number format.")
    @Size(max = 25)
    private String contactPhoneNumber;

    @Builder.Default
    private Integer views = 0;
}