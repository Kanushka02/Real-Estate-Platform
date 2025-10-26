package com.realestate.repository;

import com.realestate.model.Favorite;
import com.realestate.model.Property;
import com.realestate.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    List<Favorite> findByUser(User user);
    List<Favorite> findByUserId(Long userId);
    Optional<Favorite> findByUserAndProperty(User user, Property property);
    Boolean existsByUserAndProperty(User user, Property property);
    void deleteByUserAndProperty(User user, Property property);
}

