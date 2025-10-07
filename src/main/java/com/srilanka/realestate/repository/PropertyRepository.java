package com.srilanka.realestate.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.srilanka.realestate.entity.Property;
import com.srilanka.realestate.entity.User;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {

    // Find properties by seller (user's own properties)
    List<Property> findBySeller(User seller);

    // Find properties by status
    List<Property> findByStatus(String status);
    Page<Property> findByStatus(String status, Pageable pageable);

    // Find properties by property type (SALE or RENT)
    List<Property> findByPropertyType(String propertyType);

    // Find properties by price range
    List<Property> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

    // Find properties by location district
    @Query("SELECT p FROM Property p WHERE p.location.district = :district AND p.status = 'ACTIVE'")
    List<Property> findByLocationDistrict(@Param("district") String district);

    // Find properties by location city
    @Query("SELECT p FROM Property p WHERE p.location.city = :city AND p.status = 'ACTIVE'")
    List<Property> findByLocationCity(@Param("city") String city);

    // Find properties by number of bedrooms
    List<Property> findByBedroomsAndStatus(Integer bedrooms, String status);

    // Search properties by title or description
    @Query("SELECT p FROM Property p WHERE " +
            "(LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
            "p.status = 'ACTIVE'")
    List<Property> findByTitleOrDescriptionContaining(@Param("keyword") String keyword);

    // Advanced property search with multiple filters
    @Query("SELECT p FROM Property p WHERE " +
            "(:district IS NULL OR p.location.district = :district) AND " +
            "(:city IS NULL OR p.location.city = :city) AND " +
            "(:propertyType IS NULL OR p.propertyType = :propertyType) AND " +
            "(:minBedrooms IS NULL OR p.bedrooms >= :minBedrooms) AND " +
            "(:maxBedrooms IS NULL OR p.bedrooms <= :maxBedrooms) AND " +
            "p.status = 'ACTIVE'")
    List<Property> findPropertiesWithFilters(@Param("district") String district,
                                             @Param("city") String city,
                                             @Param("propertyType") String propertyType,
                                             @Param("minBedrooms") Integer minBedrooms,
                                             @Param("maxBedrooms") Integer maxBedrooms);

    // Count methods for admin dashboard
    long countByStatus(String status);
    long countByPropertyType(String propertyType);

    // Find recent properties for admin dashboard
    List<Property> findTop10ByOrderByCreatedAtDesc();

    // Find featured properties
    List<Property> findByFeaturedTrueAndStatus(String status);

    // Find properties for sale in active status
    @Query("SELECT p FROM Property p WHERE p.propertyType = 'SALE' AND p.status = 'ACTIVE'")
    List<Property> findActivePropertiesForSale();

    // Find properties for rent in active status
    @Query("SELECT p FROM Property p WHERE p.propertyType = 'RENT' AND p.status = 'ACTIVE'")
    List<Property> findActivePropertiesForRent();

    // Find properties by seller role (all properties from sellers)
    @Query("SELECT p FROM Property p WHERE p.seller.role = :role")
    List<Property> findBySellerRole(@Param("role") String role);

    // Find properties by price range and location
    @Query("SELECT p FROM Property p WHERE " +
            "p.price BETWEEN :minPrice AND :maxPrice AND " +
            "p.location.district = :district AND " +
            "p.status = 'ACTIVE'")
    List<Property> findByPriceRangeAndDistrict(@Param("minPrice") BigDecimal minPrice,
                                               @Param("maxPrice") BigDecimal maxPrice,
                                               @Param("district") String district);
}