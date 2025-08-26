package com.srilanka.realestate.dto;

import java.math.BigDecimal;

public class PropertyCreateDTO {
    private String title;
    private String description;
    private BigDecimal price;
    private String propertyType; // SALE or RENT
    private Integer bedrooms;
    private Integer bathrooms;
    private Integer areaSqft;
    private String address;
    private Long locationId;
    private Long categoryId;

    // Default constructor
    public PropertyCreateDTO() {}

    // Full constructor
    public PropertyCreateDTO(String title, String description, BigDecimal price, 
                           String propertyType, Integer bedrooms, Integer bathrooms, 
                           Integer areaSqft, String address, Long locationId, Long categoryId) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.propertyType = propertyType;
        this.bedrooms = bedrooms;
        this.bathrooms = bathrooms;
        this.areaSqft = areaSqft;
        this.address = address;
        this.locationId = locationId;
        this.categoryId = categoryId;
    }

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(String propertyType) {
        this.propertyType = propertyType;
    }

    public Integer getBedrooms() {
        return bedrooms;
    }

    public void setBedrooms(Integer bedrooms) {
        this.bedrooms = bedrooms;
    }

    public Integer getBathrooms() {
        return bathrooms;
    }

    public void setBathrooms(Integer bathrooms) {
        this.bathrooms = bathrooms;
    }

    public Integer getAreaSqft() {
        return areaSqft;
    }

    public void setAreaSqft(Integer areaSqft) {
        this.areaSqft = areaSqft;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    @Override
    public String toString() {
        return "PropertyCreateDTO{" +
                "title='" + title + '\'' +
                ", price=" + price +
                ", propertyType='" + propertyType + '\'' +
                ", bedrooms=" + bedrooms +
                ", bathrooms=" + bathrooms +
                ", locationId=" + locationId +
                ", categoryId=" + categoryId +
                '}';
    }
}