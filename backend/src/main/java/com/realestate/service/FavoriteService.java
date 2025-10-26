package com.realestate.service;

import com.realestate.dto.PropertyDTO;
import com.realestate.model.Favorite;
import com.realestate.model.Property;
import com.realestate.model.User;
import com.realestate.repository.FavoriteRepository;
import com.realestate.repository.PropertyRepository;
import com.realestate.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FavoriteService {
    
    @Autowired
    private FavoriteRepository favoriteRepository;
    
    @Autowired
    private PropertyRepository propertyRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PropertyService propertyService;
    
    @Transactional
    public void addFavorite(Long propertyId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Property not found"));
        
        if (favoriteRepository.existsByUserAndProperty(user, property)) {
            throw new RuntimeException("Property already in favorites");
        }
        
        Favorite favorite = new Favorite();
        favorite.setUser(user);
        favorite.setProperty(property);
        favoriteRepository.save(favorite);
    }
    
    @Transactional
    public void removeFavorite(Long propertyId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Property not found"));
        
        favoriteRepository.deleteByUserAndProperty(user, property);
    }
    
    public List<PropertyDTO> getUserFavorites(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        return favoriteRepository.findByUser(user).stream()
                .map(favorite -> propertyService.convertToDTO(favorite.getProperty()))
                .collect(Collectors.toList());
    }
    
    public boolean isFavorite(Long propertyId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Property not found"));
        
        return favoriteRepository.existsByUserAndProperty(user, property);
    }
}

