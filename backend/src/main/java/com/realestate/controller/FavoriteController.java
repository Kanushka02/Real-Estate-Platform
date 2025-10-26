package com.realestate.controller;

import com.realestate.dto.PropertyDTO;
import com.realestate.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/favorites")
@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
public class FavoriteController {
    
    @Autowired
    private FavoriteService favoriteService;
    
    @PostMapping("/{propertyId}")
    public ResponseEntity<?> addFavorite(@PathVariable Long propertyId, Authentication authentication) {
        String username = authentication.getName();
        favoriteService.addFavorite(propertyId, username);
        return ResponseEntity.ok().build();
    }
    
    @DeleteMapping("/{propertyId}")
    public ResponseEntity<?> removeFavorite(@PathVariable Long propertyId, Authentication authentication) {
        String username = authentication.getName();
        favoriteService.removeFavorite(propertyId, username);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping
    public ResponseEntity<List<PropertyDTO>> getUserFavorites(Authentication authentication) {
        String username = authentication.getName();
        List<PropertyDTO> favorites = favoriteService.getUserFavorites(username);
        return ResponseEntity.ok(favorites);
    }
    
    @GetMapping("/{propertyId}/check")
    public ResponseEntity<Boolean> isFavorite(@PathVariable Long propertyId, Authentication authentication) {
        String username = authentication.getName();
        boolean isFavorite = favoriteService.isFavorite(propertyId, username);
        return ResponseEntity.ok(isFavorite);
    }
}

