package com.srilanka.realestate.repository;

import com.srilanka.realestate.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Spring Boot automatically provides these methods:
    // save(user) - saves a user
    // findById(id) - finds user by ID
    // findAll() - gets all users
    // deleteById(id) - deletes user by ID

    // Custom methods we're adding:

    // Find user by email (for login)
    Optional<User> findByEmail(String email);

    // Find users by role (BUYER, SELLER, ADMIN)
    List<User> findByRole(String role);

    // Find active users only
    List<User> findByIsActiveTrue();

    // Find users by first name (like search)
    List<User> findByFirstNameContainingIgnoreCase(String firstName);

    // Custom query - count users by role
    @Query("SELECT COUNT(u) FROM User u WHERE u.role = ?1")
    Long countByRole(String role);

    // Check if email already exists (for registration)
    boolean existsByEmail(String email);
}
