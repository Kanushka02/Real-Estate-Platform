package com.realestate.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "properties")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Property {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Column(nullable = false)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PropertyType type;
    
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ListingType listingType;
    
    @NotNull
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;
    
    @NotBlank
    @Column(nullable = false)
    private String address;
    
    @NotBlank
    @Column(nullable = false)
    private String city;
    
    @NotBlank
    @Column(nullable = false)
    private String district;
    
    private Integer bedrooms;
    private Integer bathrooms;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal landSize; // in perches
    
    @Column(precision = 10, scale = 2)
    private BigDecimal floorSize; // in sq ft
    
    private Integer parkingSpaces;
    
    @Column(columnDefinition = "TEXT")
    private String features; // JSON string or comma-separated
    
    @Column(columnDefinition = "TEXT")
    private String images; // JSON array of image URLs
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PropertyStatus status = PropertyStatus.PENDING;
    
    @Column(nullable = false)
    private Boolean featured = false;
    
    private LocalDateTime approvedAt;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();
    
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

