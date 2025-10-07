package com.srilanka.realestate.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

   /*
           * Get all active properties (public access)
     */
    public List<Property> getAllActiveProperties() {
        return propertyRepository.findByStatus("ACTIVE");
    }

    public Page<Property> getAllActiveProperties(Pageable pageable) {
        return propertyRepository.findByStatus("ACTIVE", pageable);
    }

    /**
     * Get all properties including inactive (admin only)
     */
    public List<Property> getAllProperties() {
        return propertyRepository.findAll();
    }

    /**
     * Get property by ID (public access)
     */
    public Optional<Property> getPropertyById(Long propertyId) {
        return propertyRepository.findById(propertyId);
    }

    /**
     * Search properties with filters
     */
    public List<Property> searchProperties(String district, String city, String propertyType,
                                           Integer minBedrooms, Integer maxBedrooms) {
        return propertyRepository.findPropertiesWithFilters(
                district, city, propertyType, minBedrooms, maxBedrooms
        );
    }

    /**
     * Search properties by keyword
     */
    public List<Property> searchPropertiesByKeyword(String keyword) {
        return propertyRepository.findByTitleOrDescriptionContaining(keyword);
    }

    /**
     * Get properties by district
     */
    public List<Property> getPropertiesByDistrict(String district) {
        return propertyRepository.findByLocationDistrict(district);
    }

    /**
     * Get properties by city
     */
    public List<Property> getPropertiesByCity(String city) {
        return propertyRepository.findByLocationCity(city);
    }

    /**
     * Get properties by bedrooms
     */
    public List<Property> getPropertiesByBedrooms(Integer bedrooms) {
        return propertyRepository.findByBedroomsAndStatus(bedrooms, "ACTIVE");
    }

    /**
     * Get properties for sale
     */
    public List<Property> getPropertiesForSale() {
        return propertyRepository.findActivePropertiesForSale();
    }

    /**
     * Get properties for rent
     */
    public List<Property> getPropertiesForRent() {
        return propertyRepository.findActivePropertiesForRent();
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

    /**
     * Mark property as sold (seller or admin only)
     */
    public Property markPropertyAsSold(Long propertyId, String userEmail) {
        User user = userDetailsService.getUserByEmail(userEmail);
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Property not found"));

        // Check ownership or admin rights
        if (!property.getSeller().getUserId().equals(user.getUserId()) &&
                !user.getRole().equals("ADMIN")) {
            throw new AccessDeniedException("You can only modify your own properties");
        }

        property.setStatus("SOLD");
        property.setUpdatedAt(LocalDateTime.now());

        return propertyRepository.save(property);
    }

    /**
     * Mark property as rented (seller or admin only)
     */
    public Property markPropertyAsRented(Long propertyId, String userEmail) {
        User user = userDetailsService.getUserByEmail(userEmail);
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Property not found"));

        // Check ownership or admin rights
        if (!property.getSeller().getUserId().equals(user.getUserId()) &&
                !user.getRole().equals("ADMIN")) {
            throw new AccessDeniedException("You can only modify your own properties");
        }

        property.setStatus("RENTED");
        property.setUpdatedAt(LocalDateTime.now());

        return propertyRepository.save(property);
    }

    /**
     * Get featured properties
     */
    public List<Property> getFeaturedProperties() {
        return propertyRepository.findByFeaturedTrueAndStatus("ACTIVE");
    }

    /**
     * Set property as featured (admin only)
     */
    public Property setPropertyFeatured(Long propertyId, boolean featured, String userEmail) {
        User user = userDetailsService.getUserByEmail(userEmail);

        // Only admin can set featured properties
        if (!user.getRole().equals("ADMIN")) {
            throw new AccessDeniedException("Only admins can set featured properties");
        }

        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Property not found"));

        property.setFeatured(featured);
        property.setUpdatedAt(LocalDateTime.now());

        return propertyRepository.save(property);
    }

    /**
     * Admin: change property status to any allowed value without ownership checks
     */
    public Property adminChangePropertyStatus(Long propertyId, String newStatus) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Property not found"));
        property.setStatus(newStatus.toUpperCase());
        property.setUpdatedAt(LocalDateTime.now());
        return propertyRepository.save(property);
    }

    /**
     * Get statistics for dashboard
     */
    public PropertyStats getPropertyStats() {
        long totalProperties = propertyRepository.count();
        long activeProperties = propertyRepository.countByStatus("ACTIVE");
        long soldProperties = propertyRepository.countByStatus("SOLD");
        long rentedProperties = propertyRepository.countByStatus("RENTED");
        long propertiesForSale = propertyRepository.countByPropertyType("SALE");
        long propertiesForRent = propertyRepository.countByPropertyType("RENT");

        return new PropertyStats(totalProperties, activeProperties, soldProperties,
                rentedProperties, propertiesForSale, propertiesForRent);
    }

    /**
     * Inner class for property statistics
     */
    public static class PropertyStats {
        private final long totalProperties;
        private final long activeProperties;
        private final long soldProperties;
        private final long rentedProperties;
        private final long propertiesForSale;
        private final long propertiesForRent;

        public PropertyStats(long totalProperties, long activeProperties, long soldProperties,
                             long rentedProperties, long propertiesForSale, long propertiesForRent) {
            this.totalProperties = totalProperties;
            this.activeProperties = activeProperties;
            this.soldProperties = soldProperties;
            this.rentedProperties = rentedProperties;
            this.propertiesForSale = propertiesForSale;
            this.propertiesForRent = propertiesForRent;
        }

        // Getters
        public long getTotalProperties() { return totalProperties; }
        public long getActiveProperties() { return activeProperties; }
        public long getSoldProperties() { return soldProperties; }
        public long getRentedProperties() { return rentedProperties; }
        public long getPropertiesForSale() { return propertiesForSale; }
        public long getPropertiesForRent() { return propertiesForRent; }
    }
}