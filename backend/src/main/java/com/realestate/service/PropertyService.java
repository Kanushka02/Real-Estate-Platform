package com.realestate.service;

import com.realestate.dto.PropertyDTO;
import com.realestate.model.Property;
import com.realestate.model.User;
import com.realestate.repository.PropertyRepository;
import com.realestate.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PropertyService {
    
    @Autowired
    private PropertyRepository propertyRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    public PropertyDTO convertToDTO(Property property) {
        PropertyDTO dto = new PropertyDTO();
        dto.setId(property.getId());
        dto.setTitle(property.getTitle());
        dto.setDescription(property.getDescription());
        dto.setType(property.getType());
        dto.setListingType(property.getListingType());
        dto.setPrice(property.getPrice());
        dto.setAddress(property.getAddress());
        dto.setCity(property.getCity());
        dto.setDistrict(property.getDistrict());
        dto.setBedrooms(property.getBedrooms());
        dto.setBathrooms(property.getBathrooms());
        dto.setLandSize(property.getLandSize());
        dto.setFloorSize(property.getFloorSize());
        dto.setParkingSpaces(property.getParkingSpaces());
        dto.setFeatures(property.getFeatures());
        dto.setImages(property.getImages());
        dto.setOwnerId(property.getOwner().getId());
        dto.setOwnerName(property.getOwner().getUsername());
        dto.setStatus(property.getStatus());
        dto.setFeatured(property.getFeatured());
        dto.setCreatedAt(property.getCreatedAt());
        dto.setUpdatedAt(property.getUpdatedAt());
        return dto;
    }
    
    @Transactional
    public PropertyDTO createProperty(PropertyDTO propertyDTO, String username) {
        User owner = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Property property = new Property();
        property.setTitle(propertyDTO.getTitle());
        property.setDescription(propertyDTO.getDescription());
        property.setType(propertyDTO.getType());
        property.setListingType(propertyDTO.getListingType());
        property.setPrice(propertyDTO.getPrice());
        property.setAddress(propertyDTO.getAddress());
        property.setCity(propertyDTO.getCity());
        property.setDistrict(propertyDTO.getDistrict());
        property.setBedrooms(propertyDTO.getBedrooms());
        property.setBathrooms(propertyDTO.getBathrooms());
        property.setLandSize(propertyDTO.getLandSize());
        property.setFloorSize(propertyDTO.getFloorSize());
        property.setParkingSpaces(propertyDTO.getParkingSpaces());
        property.setFeatures(propertyDTO.getFeatures());
        property.setImages(propertyDTO.getImages());
        property.setOwner(owner);
        property.setStatus(Property.PropertyStatus.PENDING);
        
        Property saved = propertyRepository.save(property);
        return convertToDTO(saved);
    }
    
    @Transactional
    public PropertyDTO updateProperty(Long id, PropertyDTO propertyDTO, String username) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Property not found"));
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Check if user is the owner
        if (!property.getOwner().getId().equals(user.getId())) {
            throw new RuntimeException("You are not authorized to update this property");
        }
        
        property.setTitle(propertyDTO.getTitle());
        property.setDescription(propertyDTO.getDescription());
        property.setType(propertyDTO.getType());
        property.setListingType(propertyDTO.getListingType());
        property.setPrice(propertyDTO.getPrice());
        property.setAddress(propertyDTO.getAddress());
        property.setCity(propertyDTO.getCity());
        property.setDistrict(propertyDTO.getDistrict());
        property.setBedrooms(propertyDTO.getBedrooms());
        property.setBathrooms(propertyDTO.getBathrooms());
        property.setLandSize(propertyDTO.getLandSize());
        property.setFloorSize(propertyDTO.getFloorSize());
        property.setParkingSpaces(propertyDTO.getParkingSpaces());
        property.setFeatures(propertyDTO.getFeatures());
        property.setImages(propertyDTO.getImages());
        
        Property updated = propertyRepository.save(property);
        return convertToDTO(updated);
    }
    
    @Transactional
    public void deleteProperty(Long id, String username) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Property not found"));
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (!property.getOwner().getId().equals(user.getId())) {
            throw new RuntimeException("You are not authorized to delete this property");
        }
        
        propertyRepository.delete(property);
    }
    
    public PropertyDTO getPropertyById(Long id) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Property not found"));
        return convertToDTO(property);
    }
    
    public Page<PropertyDTO> getAllProperties(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return propertyRepository.findByStatus(Property.PropertyStatus.APPROVED, pageable)
                .map(this::convertToDTO);
    }
    
    public Page<PropertyDTO> getMyProperties(String username, int page, int size) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return propertyRepository.findByOwner(user, pageable)
                .map(this::convertToDTO);
    }
    
    public Page<PropertyDTO> searchProperties(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return propertyRepository.searchProperties(keyword, Property.PropertyStatus.APPROVED, pageable)
                .map(this::convertToDTO);
    }
    
    public Page<PropertyDTO> filterProperties(
            Property.PropertyType type,
            Property.ListingType listingType,
            String district,
            String city,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Integer minBedrooms,
            int page,
            int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return propertyRepository.findByFilters(
                type, listingType, district, city, minPrice, maxPrice, minBedrooms,
                Property.PropertyStatus.APPROVED, pageable
        ).map(this::convertToDTO);
    }
    
    public List<PropertyDTO> getLatestProperties() {
        return propertyRepository.findTop10ByStatusOrderByCreatedAtDesc(Property.PropertyStatus.APPROVED)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<PropertyDTO> getFeaturedProperties() {
        return propertyRepository.findByFeaturedTrueAndStatus(Property.PropertyStatus.APPROVED)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    // Admin methods
    @Transactional
    public PropertyDTO approveProperty(Long id) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Property not found"));
        
        property.setStatus(Property.PropertyStatus.APPROVED);
        property.setApprovedAt(LocalDateTime.now());
        
        Property updated = propertyRepository.save(property);
        return convertToDTO(updated);
    }
    
    @Transactional
    public PropertyDTO rejectProperty(Long id) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Property not found"));
        
        property.setStatus(Property.PropertyStatus.REJECTED);
        
        Property updated = propertyRepository.save(property);
        return convertToDTO(updated);
    }
    
    @Transactional
    public void adminDeleteProperty(Long id) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Property not found"));
        propertyRepository.delete(property);
    }
    
    public Page<PropertyDTO> getAllPropertiesAdmin(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return propertyRepository.findAll(pageable)
                .map(this::convertToDTO);
    }
}

