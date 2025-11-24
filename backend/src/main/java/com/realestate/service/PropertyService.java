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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
        dto.id = property.id;
        dto.title = property.title;
        dto.description = property.description;
        dto.type = property.type;
        dto.listingType = property.listingType;
        dto.price = property.price;
        dto.address = property.address;
        dto.city = property.city;
        dto.district = property.district;
        dto.bedrooms = property.bedrooms;
        dto.bathrooms = property.bathrooms;
        dto.landSize = property.landSize;
        dto.floorSize = property.floorSize;
        dto.parkingSpaces = property.parkingSpaces;
        dto.imageName = property.imageName;
        dto.imageType = property.imageType;
        dto.imageData = property.imageData;
        dto.ownerId = property.owner.id;
        dto.ownerName = property.owner.username;
        dto.status = property.status;
        dto.featured = property.featured;
        dto.createdAt = property.createdAt;
        dto.updatedAt = property.updatedAt;
        return dto;
    }
    
    @Transactional
    public PropertyDTO createProperty(PropertyDTO propertyDTO, String username) {
        User owner = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Property property = new Property();
        property.title = propertyDTO.title;
        property.description = propertyDTO.description;
        property.type = propertyDTO.type;
        property.listingType = propertyDTO.listingType;
        property.price = propertyDTO.price;
        property.address = propertyDTO.address;
        property.city = propertyDTO.city;
        property.district = propertyDTO.district;
        property.bedrooms = propertyDTO.bedrooms;
        property.bathrooms = propertyDTO.bathrooms;
        property.landSize = propertyDTO.landSize;
        property.floorSize = propertyDTO.floorSize;
        property.parkingSpaces = propertyDTO.parkingSpaces;
        property.imageName = propertyDTO.imageName;
        property.imageType = propertyDTO.imageType;
        property.imageData = propertyDTO.imageData;
        property.owner = owner;
        property.status = Property.PropertyStatus.PENDING;
        
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
        if (!property.owner.id.equals(user.id)) {
            throw new RuntimeException("You are not authorized to update this property");
        }
        
        property.title = propertyDTO.title;
        property.description = propertyDTO.description;
        property.type = propertyDTO.type;
        property.listingType = propertyDTO.listingType;
        property.price = propertyDTO.price;
        property.address = propertyDTO.address;
        property.city = propertyDTO.city;
        property.district = propertyDTO.district;
        property.bedrooms = propertyDTO.bedrooms;
        property.bathrooms = propertyDTO.bathrooms;
        property.landSize = propertyDTO.landSize;
        property.floorSize = propertyDTO.floorSize;
        property.parkingSpaces = propertyDTO.parkingSpaces;
        property.imageName = propertyDTO.imageName;
        property.imageType = propertyDTO.imageType;
        property.imageData = propertyDTO.imageData;
        
        Property updated = propertyRepository.save(property);
        return convertToDTO(updated);
    }
    
    @Transactional
    public void deleteProperty(Long id, String username) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Property not found"));
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (!property.owner.id.equals(user.id)) {
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
        
        property.status = Property.PropertyStatus.APPROVED;
        property.approvedAt = LocalDateTime.now();
        
        Property updated = propertyRepository.save(property);
        return convertToDTO(updated);
    }
    
    @Transactional
    public PropertyDTO rejectProperty(Long id) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Property not found"));
        
        property.status = Property.PropertyStatus.REJECTED;
        
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

    @Transactional
    public PropertyDTO uploadPropertyImage(Long propertyId, MultipartFile image, String username) throws IOException {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Property not found"));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if user is the owner
        if (!property.owner.id.equals(user.id)) {
            throw new RuntimeException("You are not authorized to upload images for this property");
        }

        // Validate and save image
        if (image != null && !image.isEmpty()) {
            property.imageName = image.getOriginalFilename();
            property.imageType = image.getContentType();
            property.imageData = image.getBytes();
        }

        Property updated = propertyRepository.save(property);
        return convertToDTO(updated);
    }
    
    // // Photo upload methods
    // @Transactional
    // public PropertyDTO uploadPhoto(Long propertyId, MultipartFile file, Boolean isPrimary, String username) {
    //     Property property = propertyRepository.findById(propertyId)
    //             .orElseThrow(() -> new RuntimeException("Property not found"));
        
    //     User user = userRepository.findByUsername(username)
    //             .orElseThrow(() -> new RuntimeException("User not found"));
        
    //     // Check if user is the owner
    //     if (!property.owner.id.equals(user.id)) {
    //         throw new RuntimeException("You are not authorized to upload photos for this property");
    //     }
        
    //     try {
    //         // Validate file
    //         if (file.isEmpty()) {
    //             throw new RuntimeException("File is empty");
    //         }
            
    //         // Get file info
    //         String originalFilename = file.getOriginalFilename();
    //         String contentType = file.getContentType();
    //         byte[] imageData = file.getBytes();
            
    //         // Validate file type
    //         if (contentType == null || !contentType.startsWith("image/")) {
    //             throw new RuntimeException("File must be an image");
    //         }
            
    //         // Update property with new image data
    //         property.imageName = originalFilename;
    //         property.imageType = contentType;
    //         property.imageData = imageData;
            
    //         Property updated = propertyRepository.save(property);
    //         return convertToDTO(updated);
            
    //     } catch (IOException e) {
    //         throw new RuntimeException("Failed to process image file", e);
    //     }
    // }
    
    // public byte[] getPhotoData(Long propertyId, Long photoId) {
    //     Property property = propertyRepository.findById(propertyId)
    //             .orElseThrow(() -> new RuntimeException("Property not found"));
        
    //     // For now, return the single image data
    //     // In a multi-image system, you would find the specific photo by photoId
    //     if (property.imageData == null) {
    //         throw new RuntimeException("Photo not found");
    //     }
        
    //     return property.imageData;
    // }
    
    // @Transactional
    // public void deletePhoto(Long propertyId, Long photoId, String username) {
    //     Property property = propertyRepository.findById(propertyId)
    //             .orElseThrow(() -> new RuntimeException("Property not found"));
        
    //     User user = userRepository.findByUsername(username)
    //             .orElseThrow(() -> new RuntimeException("User not found"));
        
    //     // Check if user is the owner
    //     if (!property.owner.id.equals(user.id)) {
    //         throw new RuntimeException("You are not authorized to delete photos for this property");
    //     }
        
    //     // Clear image data
    //     property.imageName = null;
    //     property.imageType = null;
    //     property.imageData = null;
        
    //     propertyRepository.save(property);
    // }
    
    // @Transactional
    // public PropertyDTO setPrimaryPhoto(Long propertyId, Long photoId, String username) {
    //     Property property = propertyRepository.findById(propertyId)
    //             .orElseThrow(() -> new RuntimeException("Property not found"));
        
    //     User user = userRepository.findByUsername(username)
    //             .orElseThrow(() -> new RuntimeException("User not found"));
        
    //     // Check if user is the owner
    //     if (!property.owner.id.equals(user.id)) {
    //         throw new RuntimeException("You are not authorized to modify photos for this property");
    //     }
        
    //     // For single image system, this just ensures the image is marked as primary
    //     // In a multi-image system, you would find the specific photo and mark it as primary
    //     if (property.imageData == null) {
    //         throw new RuntimeException("No photo found to set as primary");
    //     }
        
    //     Property updated = propertyRepository.save(property);
    //     return convertToDTO(updated);
    // }
}

