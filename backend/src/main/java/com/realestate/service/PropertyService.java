package com.realestate.service;

import com.realestate.dto.PropertyDTO;
import com.realestate.model.Property;
import com.realestate.model.PropertyImage; // Import
import com.realestate.model.User;
import com.realestate.repository.PropertyRepository;
import com.realestate.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils; // Import

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.*; // Import
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PropertyService {
    
    @Autowired private PropertyRepository propertyRepository;
    @Autowired private UserRepository userRepository;

    // Define upload directory
    private final String UPLOAD_DIR = "uploads/";

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
        dto.setOwnerId(property.getOwner().getId());
        dto.setOwnerName(property.getOwner().getUsername());
        dto.setStatus(property.getStatus());
        dto.setFeatured(property.getFeatured());
        dto.setCreatedAt(property.getCreatedAt());
        dto.setUpdatedAt(property.getUpdatedAt());

        // Convert Image Entities to URLs - Handle null safely
        if (property.getImages() != null && !property.getImages().isEmpty()) {
            dto.setImageUrls(property.getImages().stream()
                    .map(PropertyImage::getImageUrl)
                    .collect(Collectors.toList()));
        } else {
            dto.setImageUrls(new ArrayList<>()); // Return empty list if no images
        }
        return dto;
    }

    @Transactional
    public PropertyDTO createProperty(PropertyDTO propertyDTO, String username) {
        User owner = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Property property = new Property();
        property.setTitle(propertyDTO.title);
        property.setDescription(propertyDTO.description);
        property.setType(propertyDTO.type);
        property.setListingType(propertyDTO.listingType);
        property.setPrice(propertyDTO.price);
        property.setAddress(propertyDTO.address);
        property.setCity(propertyDTO.city);
        property.setDistrict(propertyDTO.district);
        property.setBedrooms(propertyDTO.bedrooms);
        property.setBathrooms(propertyDTO.bathrooms);
        property.setLandSize(propertyDTO.landSize);
        property.setFloorSize(propertyDTO.floorSize);
        property.setParkingSpaces(propertyDTO.parkingSpaces);
        property.setOwner(owner);
        property.setStatus(Property.PropertyStatus.PENDING);
        
        Property saved = propertyRepository.save(property);
        return convertToDTO(saved);
    }

    // --- NEW: Method to Upload Multiple Images ---
    @Transactional
    public PropertyDTO uploadPropertyImages(Long propertyId, List<MultipartFile> files, String username) throws IOException {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Property not found"));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!property.getOwner().getId().equals(user.getId()) && 
            !user.getRoles().stream().anyMatch(r -> r.getName().name().equals("ROLE_ADMIN"))) {
            throw new RuntimeException("Not authorized");
        }

        // Ensure directory exists
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Get existing images list or create new one
        List<PropertyImage> imagesList = property.getImages();
        if (imagesList == null) {
            imagesList = new ArrayList<>();
        }

        for (MultipartFile file : files) {
            if (file.isEmpty()) continue;

            // Generate unique filename
            String fileName = UUID.randomUUID().toString() + "_" + StringUtils.cleanPath(file.getOriginalFilename());
            
            // Save to disk
            try (var inputStream = file.getInputStream()) {
                Files.copy(inputStream, uploadPath.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
            }

            // Save to DB
            PropertyImage image = new PropertyImage();
            // Point to your backend port 8083
            image.setImageUrl("http://localhost:8083/uploads/" + fileName);
            image.setProperty(property);
            
            imagesList.add(image);
        }

        // Set the updated images list back to property
        property.setImages(imagesList);
        
        // Save and flush to ensure persistence
        Property updated = propertyRepository.save(property);
        propertyRepository.flush();
        
        return convertToDTO(updated);
    }

    // Keep your existing Get/Search/Filter/Update methods here...
    // Just ensure they call convertToDTO so the URLs are included.
    
    public PropertyDTO getPropertyById(Long id) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Property not found"));
        return convertToDTO(property);
    }
    
    // Add the rest of your methods (getAllProperties, etc.) similarly...
    // I'm abbreviating to save space, but keep your existing logic, just use the new convertToDTO.
    
    public Page<PropertyDTO> getAllProperties(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return propertyRepository.findByStatus(Property.PropertyStatus.APPROVED, pageable)
                .map(this::convertToDTO);
    }
    
    // Add other methods from your original code...
    public Page<PropertyDTO> getMyProperties(String username, int page, int size) {
       User user = userRepository.findByUsername(username).orElseThrow();
       Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
       return propertyRepository.findByOwner(user, pageable).map(this::convertToDTO);
    }
    
    // Helper for search/filter...
    public Page<PropertyDTO> filterProperties(
            Property.PropertyType type, Property.ListingType listingType, String district, String city,
            BigDecimal minPrice, BigDecimal maxPrice, Integer minBedrooms, int page, int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return propertyRepository.findByFilters(
                type, listingType, district, city, minPrice, maxPrice, minBedrooms,
                Property.PropertyStatus.APPROVED, pageable
        ).map(this::convertToDTO);
    }
    
    public Page<PropertyDTO> searchProperties(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return propertyRepository.searchProperties(keyword, Property.PropertyStatus.APPROVED, pageable)
                .map(this::convertToDTO);
    }
    
    public List<PropertyDTO> getLatestProperties() {
        return propertyRepository.findTop10ByStatusOrderByCreatedAtDesc(Property.PropertyStatus.APPROVED)
                .stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public List<PropertyDTO> getFeaturedProperties() {
        return propertyRepository.findByFeaturedTrueAndStatus(Property.PropertyStatus.APPROVED)
                .stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    
    @Transactional
    public PropertyDTO updateProperty(Long id, PropertyDTO propertyDTO, String username) {
        Property property = propertyRepository.findById(id).orElseThrow();
        // ... (validation)
        property.setTitle(propertyDTO.title);
        // ... copy other fields ...
        return convertToDTO(propertyRepository.save(property));
    }
    
    @Transactional
    public void deleteProperty(Long id, String username) {
        // Your existing delete logic
        Property property = propertyRepository.findById(id).orElseThrow(() -> new RuntimeException("Property not found"));
        
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (!property.getOwner().getId().equals(user.getId()) && 
            !user.getRoles().stream().anyMatch(r -> r.getName().name().equals("ROLE_ADMIN"))) {
            throw new RuntimeException("Not authorized to delete this property");
        }
        
        propertyRepository.delete(property);
    }
    
    // Admin methods...
    public Page<PropertyDTO> getAllPropertiesAdmin(int page, int size) {
        return propertyRepository.findAll(PageRequest.of(page, size)).map(this::convertToDTO);
    }
    @Transactional public PropertyDTO approveProperty(Long id) {
        Property p = propertyRepository.findById(id).orElseThrow(); p.setStatus(Property.PropertyStatus.APPROVED); return convertToDTO(propertyRepository.save(p));
    }
    @Transactional public PropertyDTO rejectProperty(Long id) {
        Property p = propertyRepository.findById(id).orElseThrow(); p.setStatus(Property.PropertyStatus.REJECTED); return convertToDTO(propertyRepository.save(p));
    }
    @Transactional public void adminDeleteProperty(Long id) { propertyRepository.deleteById(id); }
}