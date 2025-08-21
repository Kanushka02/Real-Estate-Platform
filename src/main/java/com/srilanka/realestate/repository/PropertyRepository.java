package com.srilanka.realestate.repository;

import com.srilanka.realestate.entity.Location;
import com.srilanka.realestate.entity.Property;
import com.srilanka.realestate.entity.PropertyCategory;
import com.srilanka.realestate.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface PropertyRepository extends JpaRepository<Property, Long> {

    // Find properties by seller
    List<Property> findBySeller(User seller);

    // Find properties by location
    List<Property> findByLocation(Location location);

    // Find properties by category
    List<Property> findByCategory(PropertyCategory category);

    // Find properties by status (ACTIVE, SOLD, etc.)
    List<Property> findByStatus(String status);

    // Find active properties only
    List<Property> findByStatusAndFeaturedTrue(String status);

    // Find properties by property type (SALE or RENT)
    List<Property> findByPropertyType(String propertyType);

    // Find properties within price range
    List<Property> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

    // Find properties by number of bedrooms
    List<Property> findByBedrooms(Integer bedrooms);

    // Search properties by title (like search)
    List<Property> findByTitleContainingIgnoreCase(String title);

    // Custom query - find properties in specific district
    @Query("SELECT p FROM Property p WHERE p.location.district = :district AND p.status = 'ACTIVE'")
    List<Property> findActivePropertiesInDistrict(@Param("district") String district);

    // Custom query - find properties by multiple filters
    @Query("SELECT p FROM Property p WHERE " +
            "(:category IS NULL OR p.category.categoryName = :category) AND " +
            "(:district IS NULL OR p.location.district = :district) AND " +
            "(:propertyType IS NULL OR p.propertyType = :propertyType) AND " +
            "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
            "(:maxPrice IS NULL OR p.price <= :maxPrice) AND " +
            "p.status = 'ACTIVE'")
    List<Property> findPropertiesWithFilters(
            @Param("category") String category,
            @Param("district") String district,
            @Param("propertyType") String propertyType,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice
    );

    // Count properties by seller
    Long countBySeller(User seller);
}