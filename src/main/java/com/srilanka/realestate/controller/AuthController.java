package com.srilanka.realestate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.srilanka.realestate.dto.AuthResponse;
import com.srilanka.realestate.dto.LoginRequest;
import com.srilanka.realestate.dto.RegisterRequest;
import com.srilanka.realestate.entity.User;
import com.srilanka.realestate.repository.UserRepository;
import com.srilanka.realestate.security.JwtUtil;
import com.srilanka.realestate.service.CustomUserDetailsService;

// import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Register new user
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        try {
            // Check if email already exists
            if (userRepository.findByEmail(request.getEmail()).isPresent()) {
                return ResponseEntity.badRequest()
                        .body(new AuthResponse(null, "Email already registered", false));
            }

            // Validate role
            if (!request.getRole().equals("BUYER") && !request.getRole().equals("SELLER")) {
                return ResponseEntity.badRequest()
                        .body(new AuthResponse(null, "Role must be BUYER or SELLER", false));
            }

            // Create new user with encoded password
            User user = new User();
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());
            user.setEmail(request.getEmail());
            user.setPassword(passwordEncoder.encode(request.getPassword())); // Encode password
            user.setPhone(request.getPhone());
            user.setRole(request.getRole());
            user.setIsActive(true);

            userRepository.save(user);

            // Generate JWT token
            UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
            String token = jwtUtil.generateToken(userDetails);

            return ResponseEntity.ok(new AuthResponse(token, "Registration successful", true));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new AuthResponse(null, "Registration failed: " + e.getMessage(), false));
        }
    }

    // Login user
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        try {
            // Authenticate user
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            // Generate JWT token
            UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
            String token = jwtUtil.generateToken(userDetails);

            return ResponseEntity.ok(new AuthResponse(token, "Login successful", true));

        } catch (BadCredentialsException e) {
            return ResponseEntity.badRequest()
                    .body(new AuthResponse(null, "Invalid credentials", false));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new AuthResponse(null, "Login failed: " + e.getMessage(), false));
        }
    }

    // Validate token endpoint
    @GetMapping("/validate")
    public ResponseEntity<String> validateToken(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                String username = jwtUtil.getUsernameFromToken(token);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if (jwtUtil.validateToken(token, userDetails)) {
                    return ResponseEntity.ok("Token is valid for user: " + username);
                }
            }
            return ResponseEntity.badRequest().body("Invalid token");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Token validation failed: " + e.getMessage());
        }
    }

    // Test endpoints for quick testing
    @GetMapping("/test-register")
    public ResponseEntity<AuthResponse> testRegister() {
        RegisterRequest testUser = new RegisterRequest();
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setEmail("test@gmail.com");
        testUser.setPassword("password123");
        testUser.setPhone("0771234567");
        testUser.setRole("BUYER");

        return register(testUser);
    }

    @CrossOrigin
    @GetMapping("/test-register-2")
    public ResponseEntity<AuthResponse> testRegister2() {
        RegisterRequest testUser = new RegisterRequest();
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setEmail("john.doe@gmail.com");
        testUser.setPassword("password123");
        testUser.setPhone("0771111111");
        testUser.setRole("SELLER");

        return register(testUser);
    }

    // Flexible test registration with custom email
    @GetMapping("/test-register-custom")
    public ResponseEntity<AuthResponse> testRegisterCustom(
            @RequestParam(defaultValue = "newuser@gmail.com") String email,
            @RequestParam(defaultValue = "New") String firstName,
            @RequestParam(defaultValue = "User") String lastName) {

        RegisterRequest testUser = new RegisterRequest();
        testUser.setFirstName(firstName);
        testUser.setLastName(lastName);
        testUser.setEmail(email);
        testUser.setPassword("password123");
        testUser.setPhone("0771234567");
        testUser.setRole("BUYER");

        return register(testUser);
    }

    @GetMapping("/test-login")
    public ResponseEntity<AuthResponse> testLogin() {
        LoginRequest testLogin = new LoginRequest();
        testLogin.setEmail("test@gmail.com");
        testLogin.setPassword("password123");

        return login(testLogin);
    }
}