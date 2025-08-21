package com.srilanka.realestate.controller;

import com.srilanka.realestate.entity.Location;
import com.srilanka.realestate.entity.Property;
import com.srilanka.realestate.entity.PropertyCategory;
import com.srilanka.realestate.entity.User;
import com.srilanka.realestate.repository.LocationRepository;
import com.srilanka.realestate.repository.PropertyCategoryRepository;
import com.srilanka.realestate.repository.PropertyRepository;
import com.srilanka.realestate.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/data")
public class DataController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private PropertyCategoryRepository categoryRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    // Create all sample data at once
    @GetMapping("/create-all")
    public String createAllSampleData() {
        createLocations();
        createCategories();
        createUsers();
        createProperties();
        return "All sample data created successfully! Check /api/properties/all";
    }

    // Create Sri Lankan locations
    @GetMapping("/create-locations")
    public String createLocations() {
        // Colombo District
        locationRepository.save(new Location("Colombo", "Colombo 01"));
        locationRepository.save(new Location("Colombo", "Colombo 02"));
        locationRepository.save(new Location("Colombo", "Colombo 03"));
        locationRepository.save(new Location("Colombo", "Nugegoda"));
        locationRepository.save(new Location("Colombo", "Maharagama"));
        locationRepository.save(new Location("Colombo", "Kotte"));
        locationRepository.save(new Location("Colombo", "Dehiwala"));

        // Gampaha District
        locationRepository.save(new Location("Gampaha", "Gampaha"));
        locationRepository.save(new Location("Gampaha", "Negombo"));
        locationRepository.save(new Location("Gampaha", "Kelaniya"));

        // Kandy District
        locationRepository.save(new Location("Kandy", "Kandy"));
        locationRepository.save(new Location("Kandy", "Peradeniya"));

        // Galle District
        locationRepository.save(new Location("Galle", "Galle"));
        locationRepository.save(new Location("Galle", "Hikkaduwa"));

        return "Sri Lankan locations created!";
    }

    // Create property categories
    @GetMapping("/create-categories")
    public String createCategories() {
        categoryRepository.save(new PropertyCategory("House", "Independent houses and villas"));
        categoryRepository.save(new PropertyCategory("Apartment", "Apartments and condominiums"));
        categoryRepository.save(new PropertyCategory("Land", "Bare land for construction"));
        categoryRepository.save(new PropertyCategory("Commercial", "Commercial properties and buildings"));

        return "Property categories created!";
    }

    // Create sample users
    @GetMapping("/create-users")
    public String createUsers() {
        // Create sellers
        User seller1 = new User("Kamal", "Silva", "kamal@gmail.com", "password123");
        seller1.setRole("SELLER");
        seller1.setPhone("0771234567");
        userRepository.save(seller1);

        User seller2 = new User("Nimal", "Perera", "nimal@gmail.com", "password123");
        seller2.setRole("SELLER");
        seller2.setPhone("0779876543");
        userRepository.save(seller2);

        // Create buyers
        User buyer1 = new User("Sunil", "Fernando", "sunil@gmail.com", "password123");
        buyer1.setRole("BUYER");
        buyer1.setPhone("0765432109");
        userRepository.save(buyer1);

        User buyer2 = new User("Chamali", "Wijesinghe", "chamali@gmail.com", "password123");
        buyer2.setRole("BUYER");
        buyer2.setPhone("0712345678");
        userRepository.save(buyer2);

        // Create admin
        User admin = new User("Admin", "User", "admin@propertylk.com", "admin123");
        admin.setRole("ADMIN");
        admin.setPhone("0701234567");
        userRepository.save(admin);

        return "Sample users created!";
    }

    // Create sample properties
    @GetMapping("/create-properties")
    public String createProperties() {
        // Get data for relationships
        User seller1 = userRepository.findByEmail("kamal@gmail.com").orElse(null);
        User seller2 = userRepository.findByEmail("nimal@gmail.com").orElse(null);

        Location nugegoda = locationRepository.findByDistrictAndCity("Colombo", "Nugegoda");
        Location colombo01 = locationRepository.findByDistrictAndCity("Colombo", "Colombo 01");
        Location kandy = locationRepository.findByDistrictAndCity("Kandy", "Kandy");
        Location negombo = locationRepository.findByDistrictAndCity("Gampaha", "Negombo");

        PropertyCategory houseCategory = categoryRepository.findByCategoryName("House").orElse(null);
        PropertyCategory apartmentCategory = categoryRepository.findByCategoryName("Apartment").orElse(null);
        PropertyCategory landCategory = categoryRepository.findByCategoryName("Land").orElse(null);

        // Create Property 1 - House in Nugegoda
        Property property1 = new Property();
        property1.setTitle("Beautiful 3BR House in Nugegoda");
        property1.setDescription("Spacious 3 bedroom house with modern amenities. Close to schools and shopping centers. Perfect for families.");
        property1.setPrice(new BigDecimal("25000000")); // 25 Million LKR
        property1.setPropertyType("SALE");
        property1.setBedrooms(3);
        property1.setBathrooms(2);
        property1.setAreaSqft(1800);
        property1.setAddress("123 Nugegoda Road, Nugegoda");
        property1.setSeller(seller1);
        property1.setLocation(nugegoda);
        property1.setCategory(houseCategory);
        property1.setStatus("ACTIVE");
        property1.setCreatedAt(LocalDateTime.now());
        property1.setUpdatedAt(LocalDateTime.now());
        propertyRepository.save(property1);

        // Create Property 2 - Apartment in Colombo
        Property property2 = new Property();
        property2.setTitle("Luxury 2BR Apartment in Colombo 01");
        property2.setDescription("Modern apartment with city view. Fully furnished with swimming pool and gym facilities.");
        property2.setPrice(new BigDecimal("80000")); // 80,000 LKR per month
        property2.setPropertyType("RENT");
        property2.setBedrooms(2);
        property2.setBathrooms(2);
        property2.setAreaSqft(1200);
        property2.setAddress("456 Galle Road, Colombo 01");
        property2.setSeller(seller2);
        property2.setLocation(colombo01);
        property2.setCategory(apartmentCategory);
        property2.setStatus("ACTIVE");
        property2.setFeatured(true); // Premium listing
        property2.setCreatedAt(LocalDateTime.now());
        property2.setUpdatedAt(LocalDateTime.now());
        propertyRepository.save(property2);

        // Create Property 3 - Land in Kandy
        Property property3 = new Property();
        property3.setTitle("Prime Land for Sale in Kandy");
        property3.setDescription("20 perches of land with clear title. Perfect for building your dream home. Quiet residential area.");
        property3.setPrice(new BigDecimal("5000000")); // 5 Million LKR
        property3.setPropertyType("SALE");
        property3.setAreaSqft(8712); // 20 perches = ~8712 sq ft
        property3.setAddress("789 Kandy Road, Kandy");
        property3.setSeller(seller1);
        property3.setLocation(kandy);
        property3.setCategory(landCategory);
        property3.setStatus("ACTIVE");
        property3.setCreatedAt(LocalDateTime.now());
        property3.setUpdatedAt(LocalDateTime.now());
        propertyRepository.save(property3);

        // Create Property 4 - House for Rent in Negombo
        Property property4 = new Property();
        property4.setTitle("Beachside House for Rent in Negombo");
        property4.setDescription("2 bedroom house just 5 minutes walk from the beach. Fully furnished with AC and WiFi.");
        property4.setPrice(new BigDecimal("50000")); // 50,000 LKR per month
        property4.setPropertyType("RENT");
        property4.setBedrooms(2);
        property4.setBathrooms(1);
        property4.setAreaSqft(1000);
        property4.setAddress("Beach Road, Negombo");
        property4.setSeller(seller2);
        property4.setLocation(negombo);
        property4.setCategory(houseCategory);
        property4.setStatus("ACTIVE");
        property4.setCreatedAt(LocalDateTime.now());
        property4.setUpdatedAt(LocalDateTime.now());
        propertyRepository.save(property4);

        return "Sample properties created with realistic Sri Lankan data!";
    }
}
