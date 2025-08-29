package com.srilanka.realestate.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.srilanka.realestate.entity.Location;
import com.srilanka.realestate.entity.Property;
import com.srilanka.realestate.entity.PropertyCategory;
import com.srilanka.realestate.repository.PropertyRepository;
import com.srilanka.realestate.security.JwtUtil;
import com.srilanka.realestate.service.PropertyService;

@RestController
@RequestMapping("/api/properties")
@CrossOrigin(origins = "http://localhost:3000")
public class PropertyController {

	@Autowired
	private PropertyRepository ropertyRepository;

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private PropertyService propertyService;

	/**
	 * Get all active properties (public access)
	 */
	@GetMapping("/all")
	public ResponseEntity<List<Property>> getAllProperties() {
		List<Property> properties = propertyService.getAllActiveProperties();
		return ResponseEntity.ok(properties);
	}

	/**
	 * Get property by ID (public access)
	 */
	@GetMapping("/{propertyId}")
	public ResponseEntity<Property> getPropertyById(@PathVariable Long propertyId) {
		Optional<Property> property = propertyService.getPropertyById(propertyId);
		if (property.isPresent()) {
			return ResponseEntity.ok(property.get());
		}
		return ResponseEntity.notFound().build();
	}

	/**
	 * Search properties by keyword (public access)
	 */
	@GetMapping("/search")
	public ResponseEntity<List<Property>> searchProperties(@RequestParam String keyword) {
		List<Property> properties = propertyService.searchPropertiesByKeyword(keyword);
		return ResponseEntity.ok(properties);
	}

	/**
	 * Get properties by district (public access)
	 */
	@GetMapping("/district/{district}")
	public ResponseEntity<List<Property>> getPropertiesByDistrict(@PathVariable String district) {
		List<Property> properties = propertyService.getPropertiesByDistrict(district);
		return ResponseEntity.ok(properties);
	}

	/**
	 * Get properties by city (public access)
	 */
	@GetMapping("/city/{city}")
	public ResponseEntity<List<Property>> getPropertiesByCity(@PathVariable String city) {
		List<Property> properties = propertyService.getPropertiesByCity(city);
		return ResponseEntity.ok(properties);
	}

	/**
	 * Get properties by number of bedrooms (public access)
	 */
	@GetMapping("/bedrooms/{bedrooms}")
	public ResponseEntity<List<Property>> getPropertiesByBedrooms(@PathVariable Integer bedrooms) {
		List<Property> properties = propertyService.getPropertiesByBedrooms(bedrooms);
		return ResponseEntity.ok(properties);
	}

	/**
	 * Get properties for sale (public access)
	 */
	@GetMapping("/for-sale")
	public ResponseEntity<List<Property>> getPropertiesForSale() {
		List<Property> properties = propertyService.getPropertiesForSale();
		return ResponseEntity.ok(properties);
	}

	/**
	 * Get properties for rent (public access)
	 */
	@GetMapping("/for-rent")
	public ResponseEntity<List<Property>> getPropertiesForRent() {
		List<Property> properties = propertyService.getPropertiesForRent();
		return ResponseEntity.ok(properties);
	}

	/**
	 * Get featured properties (public access)
	 */
	@GetMapping("/featured")
	public ResponseEntity<List<Property>> getFeaturedProperties() {
		List<Property> properties = propertyService.getFeaturedProperties();
		return ResponseEntity.ok(properties);
	}

	/**
	 * Advanced search with filters (public access)
	 */
	@GetMapping("/advanced-search")
	public ResponseEntity<List<Property>> advancedSearch(
			@RequestParam(required = false) String district,
			@RequestParam(required = false) String city,
			@RequestParam(required = false) String propertyType,
			@RequestParam(required = false) Integer minBedrooms,
			@RequestParam(required = false) Integer maxBedrooms) {

		List<Property> properties = propertyService.searchProperties(
				district, city, propertyType, minBedrooms, maxBedrooms);
		return ResponseEntity.ok(properties);
	}

	/**
	 * Get all locations for dropdown (public access)
	 */
	@GetMapping("/locations")
	public ResponseEntity<List<Location>> getAllLocations() {
		List<Location> locations = propertyService.getAllLocations();
		return ResponseEntity.ok(locations);
	}

	/**
	 * Get all categories for dropdown (public access)
	 */
	@GetMapping("/categories")
	public ResponseEntity<List<PropertyCategory>> getAllCategories() {
		List<PropertyCategory> categories = propertyService.getAllCategories();
		return ResponseEntity.ok(categories);
	}

	/**
	 * Get current user's properties (authenticated users only)
	 */
	@GetMapping("/my-properties")
	@PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
	public ResponseEntity<List<Property>> getMyProperties(@RequestHeader("Authorization") String authHeader) {
		try {
			String userEmail = getUserEmailFromAuth(authHeader);
			List<Property> properties = propertyService.getUserProperties(userEmail);
			return ResponseEntity.ok(properties);
		} catch (Exception e) {
			return ResponseEntity.badRequest().build();
		}
	}

	/**
	 * Create new property (SELLER and ADMIN only)
	 */
	@PostMapping("/create")
	@PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
	public ResponseEntity<?> createProperty(@RequestBody Property property,
											 @RequestHeader("Authorization") String authHeader) {
		try {
			String userEmail = getUserEmailFromAuth(authHeader);
			Property createdProperty = propertyService.createProperty(property, userEmail);

			return ResponseEntity.ok().body("{\n" +
					"  \"success\": true,\n" +
					"  \"message\": \"Property created successfully\",\n" +
					"  \"propertyId\": " + createdProperty.getPropertyId() + ",\n" +
					"  \"title\": \"" + createdProperty.getTitle() + "\"\n" +
					"}");

		} catch (Exception e) {
			return ResponseEntity.badRequest().body("{\n" +
					"  \"success\": false,\n" +
					"  \"message\": \"Failed to create property: " + e.getMessage() + "\"\n" +
					"}");
		}
	}

	/**
	 * Update property (Owner or Admin only)
	 */
	@PutMapping("/update/{propertyId}")
	@PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
	public ResponseEntity<?> updateProperty(@PathVariable Long propertyId,
											 @RequestBody Property property,
											 @RequestHeader("Authorization") String authHeader) {
		try {
			String userEmail = getUserEmailFromAuth(authHeader);
			Property updatedProperty = propertyService.updateProperty(propertyId, property, userEmail);

			return ResponseEntity.ok().body("{\n" +
					"  \"success\": true,\n" +
					"  \"message\": \"Property updated successfully\",\n" +
					"  \"propertyId\": " + updatedProperty.getPropertyId() + "\n" +
					"}");

		} catch (Exception e) {
			return ResponseEntity.badRequest().body("{\n" +
					"  \"success\": false,\n" +
					"  \"message\": \"Failed to update property: " + e.getMessage() + "\"\n" +
					"}");
		}
	}

	/**
	 * Delete property (Owner or Admin only)
	 */
	@DeleteMapping("/delete/{propertyId}")
	@PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
	public ResponseEntity<?> deleteProperty(@PathVariable Long propertyId,
											 @RequestHeader("Authorization") String authHeader) {
		try {
			String userEmail = getUserEmailFromAuth(authHeader);
			propertyService.deleteProperty(propertyId, userEmail);

			return ResponseEntity.ok().body("{\n" +
					"  \"success\": true,\n" +
					"  \"message\": \"Property deleted successfully\"\n" +
					"}");

		} catch (Exception e) {
			return ResponseEntity.badRequest().body("{\n" +
					"  \"success\": false,\n" +
					"  \"message\": \"Failed to delete property: " + e.getMessage() + "\"\n" +
					"}");
		}
	}

	/**
	 * Toggle property status (ACTIVE/INACTIVE)
	 */
	@PatchMapping("/toggle-status/{propertyId}")
	@PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
	public ResponseEntity<?> togglePropertyStatus(@PathVariable Long propertyId,
												   @RequestHeader("Authorization") String authHeader) {
		try {
			String userEmail = getUserEmailFromAuth(authHeader);
			Property property = propertyService.togglePropertyStatus(propertyId, userEmail);

			return ResponseEntity.ok().body("{\n" +
					"  \"success\": true,\n" +
					"  \"message\": \"Property status changed to " + property.getStatus() + "\",\n" +
					"  \"newStatus\": \"" + property.getStatus() + "\"\n" +
					"}");

		} catch (Exception e) {
			return ResponseEntity.badRequest().body("{\n" +
					"  \"success\": false,\n" +
					"  \"message\": \"Failed to toggle status: " + e.getMessage() + "\"\n" +
					"}");
		}
	}

	/**
	 * Mark property as sold
	 */
	@PatchMapping("/mark-sold/{propertyId}")
	@PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
	public ResponseEntity<?> markPropertyAsSold(@PathVariable Long propertyId,
												  @RequestHeader("Authorization") String authHeader) {
		try {
			String userEmail = getUserEmailFromAuth(authHeader);
			Property property = propertyService.markPropertyAsSold(propertyId, userEmail);

			return ResponseEntity.ok().body("{\n" +
					"  \"success\": true,\n" +
					"  \"message\": \"Property marked as sold\",\n" +
					"  \"propertyId\": " + property.getPropertyId() + "\n" +
					"}");

		} catch (Exception e) {
			return ResponseEntity.badRequest().body("{\n" +
					"  \"success\": false,\n" +
					"  \"message\": \"Failed to mark as sold: " + e.getMessage() + "\"\n" +
					"}");
		}
	}

	/**
	 * Mark property as rented
	 */
	@PatchMapping("/mark-rented/{propertyId}")
	@PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
	public ResponseEntity<?> markPropertyAsRented(@PathVariable Long propertyId,
													 @RequestHeader("Authorization") String authHeader) {
		try {
			String userEmail = getUserEmailFromAuth(authHeader);
			Property property = propertyService.markPropertyAsRented(propertyId, userEmail);

			return ResponseEntity.ok().body("{\n" +
					"  \"success\": true,\n" +
					"  \"message\": \"Property marked as rented\",\n" +
					"  \"propertyId\": " + property.getPropertyId() + "\n" +
					"}");

		} catch (Exception e) {
			return ResponseEntity.badRequest().body("{\n" +
					"  \"success\": false,\n" +
					"  \"message\": \"Failed to mark as rented: " + e.getMessage() + "\"\n" +
					"}");
		}
	}

	/**
	 * Set property as featured (Admin only)
	 */
	@PatchMapping("/set-featured/{propertyId}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> setPropertyFeatured(@PathVariable Long propertyId,
												  @RequestParam boolean featured,
												  @RequestHeader("Authorization") String authHeader) {
		try {
			String userEmail = getUserEmailFromAuth(authHeader);
			Property property = propertyService.setPropertyFeatured(propertyId, featured, userEmail);

			String message = featured ? "Property set as featured" : "Property removed from featured";

			return ResponseEntity.ok().body("{\n" +
					"  \"success\": true,\n" +
					"  \"message\": \"" + message + "\",\n" +
					"  \"propertyId\": " + property.getPropertyId() + ",\n" +
					"  \"featured\": " + property.getFeatured() + "\n" +
					"}");

		} catch (Exception e) {
			return ResponseEntity.badRequest().body("{\n" +
					"  \"success\": false,\n" +
					"  \"message\": \"Failed to update featured status: " + e.getMessage() + "\"\n" +
					"}");
		}
	}

	/**
	 * Test endpoint to create a sample property for authenticated user
	 */
	@GetMapping("/test-create")
	@PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
	public ResponseEntity<?> testCreateProperty(@RequestHeader("Authorization") String authHeader) {
		try {
			// Get the first location and category for test property
			List<Location> locations = propertyService.getAllLocations();
			List<PropertyCategory> categories = propertyService.getAllCategories();

			if (locations.isEmpty() || categories.isEmpty()) {
				return ResponseEntity.badRequest().body("{\n" +
						"  \"success\": false,\n" +
						"  \"message\": \"Please create sample data first using /api/data/create-all\"\n" +
						"}");
			}

			// Create a test property
			Property testProperty = new Property();
			testProperty.setTitle("Test Property from Authenticated User");
			testProperty.setDescription("This property was created using JWT authentication by the logged-in user");
			testProperty.setPrice(new java.math.BigDecimal("18000000"));
			testProperty.setPropertyType("SALE");
			testProperty.setBedrooms(3);
			testProperty.setBathrooms(2);
			testProperty.setAreaSqft(1500);
			testProperty.setAddress("Test Address, Authenticated User Property");
			testProperty.setLocation(locations.get(0));
			testProperty.setCategory(categories.get(0));

			return createProperty(testProperty, authHeader);

		} catch (Exception e) {
			return ResponseEntity.badRequest().body("{\n" +
					"  \"success\": false,\n" +
					"  \"message\": \"Test creation failed: " + e.getMessage() + "\"\n" +
					"}");
		}
	}

	/**
	 * Helper method to extract email from Authorization header
	 */
	private String getUserEmailFromAuth(String authHeader) {
		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			String token = authHeader.substring(7);
			return jwtUtil.getUsernameFromToken(token);
		}
		throw new RuntimeException("Invalid authorization header");
	}
}