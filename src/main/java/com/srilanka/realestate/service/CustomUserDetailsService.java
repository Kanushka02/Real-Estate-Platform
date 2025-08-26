package com.srilanka.realestate.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.srilanka.realestate.entity.User;
import com.srilanka.realestate.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
	private UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

		GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + user.getRole());
		return new org.springframework.security.core.userdetails.User(
				user.getEmail(),
				user.getPassword(),
				user.getIsActive(),
				true, // accountNonExpired
				true, // credentialsNonExpired
				true, // accountNonLocked
				List.of(authority)
		);
	}

	public List<User> getAllUsers() {
		return userRepository.findAll();
	}

	public List<User> getUsersByRole(String role) {
		return userRepository.findByRole(role);
	}

	public User getUserById(Long userId) {
		return userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("User not found"));
	}

	public User getUserByEmail(String email) {
		return userRepository.findByEmail(email)
				.orElseThrow(() -> new RuntimeException("User not found"));
	}

	public boolean existsByEmail(String email) {
		return userRepository.findByEmail(email).isPresent();
	}

	public User updateUser(User user) {
		return userRepository.save(user);
    }
}