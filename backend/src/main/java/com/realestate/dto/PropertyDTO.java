package com.realestate.dto;

import com.realestate.model.Property;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PropertyDTO {
    private Long id;
    
    @NotBlank
    private String title;
    
    private String description;
    
    @NotNull
    private Property.PropertyType type;
    
    @NotNull
    private Property.ListingType listingType;
    
    @NotNull
    private BigDecimal price;
    
    @NotBlank
    private String address;
    
    @NotBlank
    private String city;
    
    @NotBlank
    private String district;
    
    private Integer bedrooms;
    private Integer bathrooms;
    private BigDecimal landSize;
    private BigDecimal floorSize;
    private Integer parkingSpaces;
    private String features;
    private String images;
    
    private Long ownerId;
    private String ownerName;
    
    private Property.PropertyStatus status;
    private Boolean featured;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

