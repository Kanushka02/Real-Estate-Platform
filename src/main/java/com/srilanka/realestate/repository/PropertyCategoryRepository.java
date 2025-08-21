package com.srilanka.realestate.repository;

import com.srilanka.realestate.entity.PropertyCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PropertyCategoryRepository extends JpaRepository<PropertyCategory, Long> {

    // Find category by name
    Optional<PropertyCategory> findByCategoryName(String categoryName);

    // Check if category exists by name
    boolean existsByCategoryName(String categoryName);
}
