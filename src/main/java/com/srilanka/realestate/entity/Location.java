package com.srilanka.realestate.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "locations")
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long locationId;

    @Column(name = "district", nullable = false, length = 50)
    private String district; // Colombo, Kandy, Galle, etc.

    @Column(name = "city", nullable = false, length = 50)
    private String city; // Nugegoda, Maharagama, etc.

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Constructors
    public Location() {
    }

    public Location(String district, String city) {
        this.district = district;
        this.city = city;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}