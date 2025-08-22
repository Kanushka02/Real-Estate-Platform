package com.srilanka.realestate.controller;

import com.srilanka.realestate.entity.User;
import com.srilanka.realestate.dto.AuthResponse;
import com.srilanka.realestate.repository.UserRepository;
import com.srilanka.realestate.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import com.srilanka.realestate.dto.LoginRequest;    
import com.srilanka.realestate.dto.RegisterRequest;

import java.util.Optional;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*") // Allow React frontend to access
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // User Registration
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        try {
            // Check if email already exists
            if (userRepository.existsByEmail(request.getEmail())) {
                return ResponseEntity.badRequest()
                        .body(new AuthResponse("Email already exists!"));
            }

            // Validate required fields
            if (request.getEmail() == null || request.getPassword() == null ||
                    request.getFirstName() == null || request.getLastName() == null) {
                return ResponseEntity.badRequest()
                        .body(new AuthResponse("All fields are required!"));
            }

            // Validate role (only BUYER or SELLER allowed for registration)
            String role = request.getRole();
            if (role == null || (!role.equals("BUYER") && !role.equals("SELLER"))) {
                role = "BUYER"; // Default to BUYER
            }

            // Create new user
            User user = new User();
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());
            user.setEmail(request.getEmail().toLowerCase());
            user.setPassword(passwordEncoder.encode(request.getPassword())); // Hash password
            user.setPhone(request.getPhone());
            user.setRole(role);
            user.setIsActive(true);
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());

            // Save user to database
            userRepository.save(user);

            // Generate JWT token
            String token = jwtUtil.generateToken(user.getEmail(), user.getRole());

            return ResponseEntity.ok(
                    new AuthResponse(token, user.getEmail(), user.getRole(),
                            "Registration successful!")
            );

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new AuthResponse("Registration failed: " + e.getMessage()));
        }
    }

    // User Login
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        try {
            // Validate input
            if (request.getEmail() == null || request.getPassword() == null) {
                return ResponseEntity.badRequest()
                        .body(new AuthResponse("Email and password are required!"));
            }

            // Find user by email
            Optional<User> userOptional = userRepository.findByEmail(request.getEmail().toLowerCase());
            if (!userOptional.isPresent()) {
                return ResponseEntity.badRequest()
                        .body(new AuthResponse("Invalid email or password!"));
            }

            User user = userOptional.get();

            // Check if user is active
            if (!user.getIsActive()) {
                return ResponseEntity.badRequest()
                        .body(new AuthResponse("Account is deactivated!"));
            }

            // Verify password
            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                return ResponseEntity.badRequest()
                        .body(new AuthResponse("Invalid email or password!"));
            }

            // Generate JWT token
            String token = jwtUtil.generateToken(user.getEmail(), user.getRole());

            // Update last login time
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);

            return ResponseEntity.ok(
                    new AuthResponse(token, user.getEmail(), user.getRole(),
                            "Login successful!")
            );

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new AuthResponse("Login failed: " + e.getMessage()));
        }
    }

    // Validate Token (check if token is still valid)
    @PostMapping("/validate")
    public ResponseEntity<AuthResponse> validateToken(@RequestHeader("Authorization") String token) {
        try {
            // Remove "Bearer " prefix if present
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            // Validate token
            if (jwtUtil.validateToken(token) && !jwtUtil.isTokenExpired(token)) {
                String email = jwtUtil.getEmailFromToken(token);
                String role = jwtUtil.getRoleFromToken(token);

                return ResponseEntity.ok(
                        new AuthResponse(token, email, role, "Token is valid")
                );
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new AuthResponse("Invalid or expired token"));
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponse("Token validation failed"));
        }
    }

    // Get current user profile
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@RequestHeader("Authorization") String token) {
        try {
            // Remove "Bearer " prefix if present
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            // Validate token
            if (!jwtUtil.validateToken(token) || jwtUtil.isTokenExpired(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new AuthResponse("Invalid or expired token"));
            }

            // Get user from token
            String email = jwtUtil.getEmailFromToken(token);
            Optional<User> userOptional = userRepository.findByEmail(email);

            if (userOptional.isPresent()) {
                User user = userOptional.get();
                // Don't return password in response
                user.setPassword(null);
                return ResponseEntity.ok(user);
            } else {
                return ResponseEntity.badRequest()
                        .body(new AuthResponse("User not found"));
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponse("Failed to get profile"));
        }


    }

}
