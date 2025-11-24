package com.realestate.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "properties")
@Data
@NoArgsConstructor
public class Property {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    
    @NotBlank
    @Column(nullable = false)
    public String title;
    
    @Column(columnDefinition = "TEXT")
    public String description;
    
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public PropertyType type;
    
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public ListingType listingType;
    
    @NotNull
    @Column(nullable = false, precision = 12, scale = 2)
    public BigDecimal price;
    
    @NotBlank
    @Column(nullable = false)
    public String address;
    
    @NotBlank
    @Column(nullable = false)
    public String city;
    
    @NotBlank
    @Column(nullable = false)
    public String district;
    
    public Integer bedrooms;
    public Integer bathrooms;
    
    @Column(precision = 10, scale = 2)
    public BigDecimal landSize; // in perches
    
    @Column(precision = 10, scale = 2)
    public BigDecimal floorSize; // in sq ft
    
    public Integer parkingSpaces;
    
    // Keep images field for backward compatibility with existing DB data
    @Column(columnDefinition = "TEXT")
    public String images; // JSON array of image URLs or base64 data
    
    // New image fields for proper photo handling
    public String imageName; // Original filename of the image
    public String imageType; // MIME type of the image (e.g., "image/jpeg", "image/png")
    
    @Lob
    public byte[] imageData; // Binary data of the image
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    public User owner;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public PropertyStatus status = PropertyStatus.PENDING;
    
    @Column(nullable = false)
    public Boolean featured = false;
    
    public LocalDateTime approvedAt;
    
    @Column(nullable = false, updatable = false)
    public LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(nullable = false)
    public LocalDateTime updatedAt = LocalDateTime.now();
    
    // No manual constructors needed - using Lombok @NoArgsConstructor
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public enum PropertyType {
        HOUSE,
        APARTMENT,
        LAND,
        COMMERCIAL,
        VILLA,
        CONDO
    }
    
    public enum ListingType {
        SALE,
        RENT
    }
    
    public enum PropertyStatus {
        PENDING,
        APPROVED,
        REJECTED,
        SOLD,
        RENTED
    }
}

