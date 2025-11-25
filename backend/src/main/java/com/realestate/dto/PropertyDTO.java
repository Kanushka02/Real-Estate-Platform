package com.realestate.dto;

import com.realestate.model.Property;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data; // Add Lombok Data for getters/setters

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data 
public class PropertyDTO {
    public Long id;
    @NotBlank public String title;
    public String description;
    @NotNull public Property.PropertyType type;
    @NotNull public Property.ListingType listingType;
    @NotNull public BigDecimal price;
    @NotBlank public String address;
    @NotBlank public String city;
    @NotBlank public String district;
    public Integer bedrooms;
    public Integer bathrooms;
    public BigDecimal landSize;
    public BigDecimal floorSize;
    public Integer parkingSpaces;
    
    // --- CHANGED: List of Image URLs ---
    public List<String> imageUrls; 
    
    public Long ownerId;
    public String ownerName;
    public Property.PropertyStatus status;
    public Boolean featured;
    public LocalDateTime createdAt;
    public LocalDateTime updatedAt;
}