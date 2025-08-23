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
                        .requestMatchers("/api/properties/district/**").permitAll()
                        .requestMatchers("/api/properties/for-sale").permitAll()
                        .requestMatchers("/api/properties/for-rent").permitAll()
                        .requestMatchers("/api/properties/bedrooms/**").permitAll()
                        .requestMatchers("/api/data/create-all").permitAll() // For testing

                        // Seller and Admin can create/update properties
                        .requestMatchers("/api/properties/create").hasAnyRole("SELLER", "ADMIN")
                        .requestMatchers("/api/properties/update/**").hasAnyRole("SELLER", "ADMIN")
                        .requestMatchers("/api/properties/delete/**").hasAnyRole("SELLER", "ADMIN")

                        // Admin only endpoints
                        .requestMatchers("/api/users/all").hasRole("ADMIN")
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // Authenticated users (Buyer, Seller, Admin) can save properties
                        .requestMatchers("/api/properties/save").authenticated()
                        .requestMatchers("/api/properties/saved").authenticated()
                        .requestMatchers("/api/users/profile").authenticated()

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