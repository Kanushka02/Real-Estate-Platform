package com.srilanka.realestate.repository;

import com.srilanka.realestate.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {

    // Find all locations in a specific district
    List<Location> findByDistrict(String district);

    // Find all locations in a specific city
    List<Location> findByCity(String city);

    // Find location by district and city
    Location findByDistrictAndCity(String district, String city);

    // Find all unique districts
    List<Location> findDistinctByDistrictIsNotNull();

}
