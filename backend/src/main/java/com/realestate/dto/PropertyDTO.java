package com.realestate.dto;

import com.realestate.model.Property;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PropertyDTO {
    public Long id;
    
    @NotBlank
    public String title;
    
    public String description;
    
    @NotNull
    public Property.PropertyType type;
    
    @NotNull
    public Property.ListingType listingType;
    
    @NotNull
    public BigDecimal price;
    
    @NotBlank
    public String address;
    
    @NotBlank
    public String city;
    
    @NotBlank
    public String district;
    
    public Integer bedrooms;
    public Integer bathrooms;
    public BigDecimal landSize;
    public BigDecimal floorSize;
    public Integer parkingSpaces;
    
  
    
    // New image fields for proper photo handling
    public String imageName; // Original filename of the image
    public String imageType; // MIME type of the image (e.g., "image/jpeg", "image/png")
    public byte[] imageData; // Binary data of the image
    
    public Long ownerId;
    public String ownerName;
    
    public Property.PropertyStatus status;
    public Boolean featured;
    
    public LocalDateTime createdAt;
    public LocalDateTime updatedAt;

    public PropertyDTO() {}
    
    // Updated constructor to include image fields
    public PropertyDTO(Long id, String title, String description, Property.PropertyType type, Property.ListingType listingType, BigDecimal price, String address, String city, String district, Integer bedrooms, Integer bathrooms, BigDecimal landSize, BigDecimal floorSize, Integer parkingSpaces, String imageName, String imageType, byte[] imageData, Long ownerId, String ownerName, Property.PropertyStatus status, Boolean featured, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.type = type;
        this.listingType = listingType;
        this.price = price;
        this.address = address;
        this.city = city;
        this.district = district;
        this.bedrooms = bedrooms;
        this.bathrooms = bathrooms;
        this.landSize = landSize;
        this.floorSize = floorSize;
        this.parkingSpaces = parkingSpaces;
        this.imageName = imageName;
        this.imageType = imageType;
        this.imageData = imageData;
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.status = status;
        this.featured = featured;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    // Keep the old constructor for backward compatibility
    public PropertyDTO(Long id, String title, String description, Property.PropertyType type, Property.ListingType listingType, BigDecimal price, String address, String city, String district, Integer bedrooms, Integer bathrooms, BigDecimal landSize, BigDecimal floorSize, Integer parkingSpaces, Long ownerId, String ownerName, Property.PropertyStatus status, Boolean featured, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.type = type;
        this.listingType = listingType;
        this.price = price;
        this.address = address;
        this.city = city;
        this.district = district;
        this.bedrooms = bedrooms;
        this.bathrooms = bathrooms;
        this.landSize = landSize;
        this.floorSize = floorSize;
        this.parkingSpaces = parkingSpaces;
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.status = status;
        this.featured = featured;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}

