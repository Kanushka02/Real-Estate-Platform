package com.realestate.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data; // Ensure Data is imported
import lombok.NoArgsConstructor; // Ensure NoArgsConstructor is imported

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "properties")
@Data
@NoArgsConstructor
@AllArgsConstructor
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
    public BigDecimal landSize; 
    
    @Column(precision = 10, scale = 2)
    public BigDecimal floorSize; 
    
    public Integer parkingSpaces;
    
    // --- CHANGED: One Property has Many Images ---
    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<PropertyImage> images = new ArrayList<>();

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
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public enum PropertyType { HOUSE, APARTMENT, LAND, COMMERCIAL, VILLA, CONDO }
    public enum ListingType { SALE, RENT }
    public enum PropertyStatus { PENDING, APPROVED, REJECTED, SOLD, RENTED }
}