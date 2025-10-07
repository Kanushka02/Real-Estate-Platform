package com.srilanka.realestate.security;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authz -> authz
                        // Public endpoints - anyone can access
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/properties/all").permitAll()
                        .requestMatchers("/api/properties/search").permitAll()
                        .requestMatchers("/api/properties/advanced-search").permitAll()
                        .requestMatchers("/api/properties/district/**").permitAll()
                        .requestMatchers("/api/properties/city/**").permitAll()
                        .requestMatchers("/api/properties/for-sale").permitAll()
                        .requestMatchers("/api/properties/for-rent").permitAll()
                        .requestMatchers("/api/properties/bedrooms/**").permitAll()
                        .requestMatchers("/api/properties/featured").permitAll()
                        .requestMatchers("/api/properties/categories").permitAll()
                        .requestMatchers("/api/properties/locations").permitAll()
                        .requestMatchers("/api/properties/{id}").permitAll()
                        .requestMatchers("/api/property-images/property/**").permitAll() // Public access to view images
                        .requestMatchers("/api/uploads/**").permitAll() // Static file access for uploaded images
                        .requestMatchers("/api/reviews/**").permitAll() // Public access to reviews
                        .requestMatchers("/api/data/**").permitAll() // For testing/dev seeding

                        // Seller and Admin can create/update properties
                        .requestMatchers("/api/properties/create").hasAnyRole("SELLER", "ADMIN")
                        .requestMatchers("/api/properties/update/**").hasAnyRole("SELLER", "ADMIN")
                        .requestMatchers("/api/properties/delete/**").hasAnyRole("SELLER", "ADMIN")

                        // Image upload endpoints (Seller and Admin only)
                        .requestMatchers("/api/property-images/upload/**").hasAnyRole("SELLER", "ADMIN")
                        .requestMatchers("/api/property-images/**").hasAnyRole("SELLER", "ADMIN")

                        // Admin only endpoints
                        .requestMatchers("/api/users/all").hasRole("ADMIN")
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // Authenticated users (Buyer, Seller, Admin) can save properties
                        .requestMatchers("/api/properties/save").authenticated()
                        .requestMatchers("/api/properties/saved").authenticated()
                        .requestMatchers("/api/users/profile").authenticated()
                        // Bookings
                        .requestMatchers("/api/bookings/create").hasAnyRole("BUYER","ADMIN")
                        .requestMatchers("/api/bookings/me").hasAnyRole("BUYER","ADMIN")
                        .requestMatchers("/api/bookings/my-listings").hasAnyRole("SELLER","ADMIN")
                        .requestMatchers("/api/bookings/**").authenticated()

                        // All other requests need authentication
                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex ->
                        ex.authenticationEntryPoint(jwtAuthenticationEntryPoint))
                .addFilterBefore(jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}