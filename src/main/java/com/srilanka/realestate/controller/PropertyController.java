package com.srilanka.realestate.controller;

import com.srilanka.realestate.entity.Property;
import com.srilanka.realestate.repository.PropertyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/properties")
public class PropertyController {

    @Autowired
    private PropertyRepository propertyRepository;

    // Get all properties
    @GetMapping("/all")
    public List<Property> getAllProperties() {
        return propertyRepository.findAll();
    }

    // Get active properties only
    @GetMapping("/active")
    public List<Property> getActiveProperties() {
        return propertyRepository.findByStatus("ACTIVE");
    }

    // Get properties for sale
    @GetMapping("/for-sale")
    public List<Property> getPropertiesForSale() {
        return propertyRepository.findByPropertyType("SALE");
    }

    // Get properties for rent
    @GetMapping("/for-rent")
    public List<Property> getPropertiesForRent() {
        return propertyRepository.findByPropertyType("RENT");
    }

    // Get featured properties
    @GetMapping("/featured")
    public List<Property> getFeaturedProperties() {
        return propertyRepository.findByStatusAndFeaturedTrue("ACTIVE");
    }

    // Get properties by district (like: /api/properties/district/Colombo)
    @GetMapping("/district/{district}")
    public List<Property> getPropertiesByDistrict(@PathVariable String district) {
        return propertyRepository.findActivePropertiesInDistrict(district);
    }

    // Get properties within price range
    @GetMapping("/price-range")
    public List<Property> getPropertiesByPriceRange(
            @RequestParam BigDecimal minPrice,
            @RequestParam BigDecimal maxPrice) {
        return propertyRepository.findByPriceBetween(minPrice, maxPrice);
    }

    // Get properties by bedrooms
    @GetMapping("/bedrooms/{bedrooms}")
    public List<Property> getPropertiesByBedrooms(@PathVariable Integer bedrooms) {
        return propertyRepository.findByBedrooms(bedrooms);
    }

    // Search properties by title
    @GetMapping("/search")
    public List<Property> searchProperties(@RequestParam String keyword) {
        return propertyRepository.findByTitleContainingIgnoreCase(keyword);
    }

    // Advanced search with multiple filters
    @GetMapping("/filter")
    public List<Property> getPropertiesWithFilters(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String district,
            @RequestParam(required = false) String propertyType,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice) {

        return propertyRepository.findPropertiesWithFilters(
                category, district, propertyType, minPrice, maxPrice);
    }

    // Get property count
    @GetMapping("/count")
    public String getPropertyCount() {
        long count = propertyRepository.count();
        return "Total properties: " + count;
    }
}
