package com.realestate.controller;

import com.realestate.dto.PropertyDTO;
import com.realestate.dto.UserDTO;
import com.realestate.service.PropertyService;
import com.realestate.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    
    @Autowired
    private PropertyService propertyService;
    
    @Autowired
    private UserService userService;
    
    // Property Management
    @GetMapping("/properties")
    public ResponseEntity<Page<PropertyDTO>> getAllProperties(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<PropertyDTO> properties = propertyService.getAllPropertiesAdmin(page, size);
        return ResponseEntity.ok(properties);
    }
    
    @PutMapping("/properties/{id}/approve")
    public ResponseEntity<PropertyDTO> approveProperty(@PathVariable Long id) {
        PropertyDTO property = propertyService.approveProperty(id);
        return ResponseEntity.ok(property);
    }
    
    @PutMapping("/properties/{id}/reject")
    public ResponseEntity<PropertyDTO> rejectProperty(@PathVariable Long id) {
        PropertyDTO property = propertyService.rejectProperty(id);
        return ResponseEntity.ok(property);
    }
    
    @DeleteMapping("/properties/{id}")
    public ResponseEntity<?> deleteProperty(@PathVariable Long id) {
        propertyService.adminDeleteProperty(id);
        return ResponseEntity.ok().build();
    }
    
    // User Management
    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/users/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable Long id) {
        UserDTO user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }
    
    @PutMapping("/users/{id}/toggle-status")
    public ResponseEntity<?> toggleUserStatus(@PathVariable Long id) {
        userService.toggleUserStatus(id);
        return ResponseEntity.ok().build();
    }
}

