package com.srilanka.realestate.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.srilanka.realestate.entity.Location;
import com.srilanka.realestate.entity.Property;
import com.srilanka.realestate.entity.PropertyCategory;
import com.srilanka.realestate.entity.User;
import com.srilanka.realestate.repository.LocationRepository;
import com.srilanka.realestate.repository.PropertyCategoryRepository;
import com.srilanka.realestate.repository.PropertyRepository;

@Service
public class PropertyService {

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private PropertyCategoryRepository categoryRepository;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    /**
     * Create a new property (only SELLER or ADMIN can create)
     */
    public Property createProperty(Property property, String userEmail) {
        User user = userDetailsService.getUserByEmail(userEmail);
        
        // Validate user can create properties
        if (!user.getRole().equals("SELLER") && !user.getRole().equals("ADMIN")) {
            throw new AccessDeniedException("Only sellers and admins can create properties");
        }

        // Set the seller
        property.setSeller(user);
        
        // Set timestamps
        property.setCreatedAt(LocalDateTime.now());
        property.setUpdatedAt(LocalDateTime.now());
        property.setStatus("ACTIVE");

        return propertyRepository.save(property);
    }

    /**
     * Update property (only owner or admin)
     */
    public Property updateProperty(Long propertyId, Property updatedProperty, String userEmail) {
        User user = userDetailsService.getUserByEmail(userEmail);
        Property existingProperty = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Property not found"));

        // Check ownership or admin rights
        if (!existingProperty.getSeller().getUserId().equals(user.getUserId()) && 
            !user.getRole().equals("ADMIN")) {
            throw new AccessDeniedException("You can only update your own properties");
        }

        // Update fields
        existingProperty.setTitle(updatedProperty.getTitle());
        existingProperty.setDescription(updatedProperty.getDescription());
        existingProperty.setPrice(updatedProperty.getPrice());
        existingProperty.setPropertyType(updatedProperty.getPropertyType());
        existingProperty.setBedrooms(updatedProperty.getBedrooms());
        existingProperty.setBathrooms(updatedProperty.getBathrooms());
        existingProperty.setAreaSqft(updatedProperty.getAreaSqft());
        existingProperty.setAddress(updatedProperty.getAddress());
        existingProperty.setLocation(updatedProperty.getLocation());
        existingProperty.setCategory(updatedProperty.getCategory());
        existingProperty.setUpdatedAt(LocalDateTime.now());

        return propertyRepository.save(existingProperty);
    }

    /**
     * Delete property (only owner or admin)
     */
    public void deleteProperty(Long propertyId, String userEmail) {
        User user = userDetailsService.getUserByEmail(userEmail);
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Property not found"));

        // Check ownership or admin rights
        if (!property.getSeller().getUserId().equals(user.getUserId()) && 
            !user.getRole().equals("ADMIN")) {
            throw new AccessDeniedException("You can only delete your own properties");
        }

        propertyRepository.delete(property);
    }

    /**
     * Get user's own properties
     */
    public List<Property> getUserProperties(String userEmail) {
        User user = userDetailsService.getUserByEmail(userEmail);
        return propertyRepository.findBySeller(user);
    }

    /**
     * Get all properties (public access)
     */
    public List<Property> getAllProperties() {
        return propertyRepository.findByStatus("ACTIVE");
    }

    /**
     * Get property by ID (public access)
     */
    public Optional<Property> getPropertyById(Long propertyId) {
        return propertyRepository.findById(propertyId);
    }

    /**
     * Search properties with filters (by location, type, bedrooms)
     */
    public List<Property> searchProperties(String district, String city, String propertyType,
                                           Integer minBedrooms, Integer maxBedrooms) {
        return propertyRepository.findPropertiesWithFilters(
                district, city, propertyType, minBedrooms, maxBedrooms
        );
    }

    /**
     * Get all locations for dropdowns
     */
    public List<Location> getAllLocations() {
        return locationRepository.findAll();
    }

    /**
     * Get all categories for dropdowns
     */
    public List<PropertyCategory> getAllCategories() {
        return categoryRepository.findAll();
    }

    /**
     * Toggle property status (ACTIVE/INACTIVE)
     */
    public Property togglePropertyStatus(Long propertyId, String userEmail) {
        User user = userDetailsService.getUserByEmail(userEmail);
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Property not found"));

        // Check ownership or admin rights
        if (!property.getSeller().getUserId().equals(user.getUserId()) && 
            !user.getRole().equals("ADMIN")) {
            throw new AccessDeniedException("You can only modify your own properties");
        }

        // Toggle status
        String newStatus = property.getStatus().equals("ACTIVE") ? "INACTIVE" : "ACTIVE";
        property.setStatus(newStatus);
        property.setUpdatedAt(LocalDateTime.now());

        return propertyRepository.save(property);
    }
}