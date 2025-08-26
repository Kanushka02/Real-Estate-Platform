package com.srilanka.realestate.repository;

import com.srilanka.realestate.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Find user by email (for authentication)
    Optional<User> findByEmail(String email);

    // Find users by role
    List<User> findByRole(String role);

    // Find active/inactive users
    List<User> findByIsActive(Boolean isActive);

    // Count methods for admin dashboard
    long countByIsActive(Boolean isActive);
    long countByRole(String role);

    // Search users by name or email (for admin search)
    List<User> findByEmailContainingIgnoreCaseOrFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
            String email, String firstName, String lastName);

    // Find recent users (for admin dashboard)
    List<User> findTop10ByOrderByCreatedAtDesc();

    // Find users by partial email match
    List<User> findByEmailContainingIgnoreCase(String email);

    // Find users by name patterns
    List<User> findByFirstNameContainingIgnoreCase(String firstName);
    List<User> findByLastNameContainingIgnoreCase(String lastName);

    // Count users created today (you can implement this with @Query if needed)
    // @Query("SELECT COUNT(u) FROM User u WHERE DATE(u.createdAt) = CURRENT_DATE")
    // long countUsersCreatedToday();
}