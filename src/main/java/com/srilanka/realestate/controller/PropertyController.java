package com.srilanka.realestate.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.srilanka.realestate.entity.Property;
import com.srilanka.realestate.repository.PropertyRepository;

@RestController
@RequestMapping("/api/properties")
public class PropertyController {

    @Autowired
    private PropertyRepository propertyRepository;

    // Get all properties
    @GetMapping("/all")
    public ResponseEntity<List<Property>> getAllProperties() {
        try {
            List<Property> properties = propertyRepository.findAll();
            return ResponseEntity.ok(properties);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Get active properties only
    @GetMapping("/active")
    public ResponseEntity<List<Property>> getActiveProperties() {
        try {
            List<Property> properties = propertyRepository.findByStatus("ACTIVE");
            return ResponseEntity.ok(properties);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Get properties for sale
    @GetMapping("/for-sale")
    public ResponseEntity<List<Property>> getPropertiesForSale() {
        try {
            List<Property> properties = propertyRepository.findByPropertyType("SALE");
            return ResponseEntity.ok(properties);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Get properties for rent
    @GetMapping("/for-rent")
    public ResponseEntity<List<Property>> getPropertiesForRent() {
        try {
            List<Property> properties = propertyRepository.findByPropertyType("RENT");
            return ResponseEntity.ok(properties);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Get featured properties
    @GetMapping("/featured")
    public ResponseEntity<List<Property>> getFeaturedProperties() {
        try {
            List<Property> properties = propertyRepository.findByFeaturedTrueAndStatus("ACTIVE");
            return ResponseEntity.ok(properties);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Get properties by district (like: /api/properties/district/Colombo)
    @GetMapping("/district/{district}")
    public ResponseEntity<List<Property>> getPropertiesByDistrict(@PathVariable String district) {
        try {
            if (district == null || district.trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            List<Property> properties = propertyRepository.findByLocationDistrict(district);
            return ResponseEntity.ok(properties);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Get properties within price range
    @GetMapping("/price-range")
    public ResponseEntity<List<Property>> getPropertiesByPriceRange(
            @RequestParam BigDecimal minPrice,
            @RequestParam BigDecimal maxPrice) {
        try {
            if (minPrice == null || maxPrice == null || minPrice.compareTo(maxPrice) > 0) {
                return ResponseEntity.badRequest().build();
            }
            List<Property> properties = propertyRepository.findByPriceBetween(minPrice, maxPrice);
            return ResponseEntity.ok(properties);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Get properties by bedrooms
    @GetMapping("/bedrooms/{bedrooms}")
    public ResponseEntity<List<Property>> getPropertiesByBedrooms(@PathVariable Integer bedrooms) {
        try {
            if (bedrooms == null || bedrooms < 0) {
                return ResponseEntity.badRequest().build();
            }
            List<Property> properties = propertyRepository.findByBedroomsAndStatus(bedrooms, "ACTIVE");
            return ResponseEntity.ok(properties);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Search properties by title
    @GetMapping("/search")
    public ResponseEntity<List<Property>> searchProperties(@RequestParam String keyword) {
        try {
            if (keyword == null || keyword.trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            List<Property> properties = propertyRepository.findByTitleOrDescriptionContaining(keyword);
            return ResponseEntity.ok(properties);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Advanced search with multiple filters (district/city/type/bedrooms)
    @GetMapping("/filter")
    public ResponseEntity<List<Property>> getPropertiesWithFilters(
            @RequestParam(required = false) String district,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String propertyType,
            @RequestParam(required = false) Integer minBedrooms,
            @RequestParam(required = false) Integer maxBedrooms) {

        try {
            if (minBedrooms != null && maxBedrooms != null && minBedrooms > maxBedrooms) {
                return ResponseEntity.badRequest().build();
            }

            List<Property> properties = propertyRepository.findPropertiesWithFilters(
                    district, city, propertyType, minBedrooms, maxBedrooms);
            return ResponseEntity.ok(properties);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Get property count
    @GetMapping("/count")
    public ResponseEntity<String> getPropertyCount() {
        try {
            long count = propertyRepository.count();
            return ResponseEntity.ok("Total properties: " + count);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
