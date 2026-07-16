package com.project.hospital.config;

import com.project.hospital.entity.User;
import com.project.hospital.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Bridges Spring Security's authentication with our UserRepository.
 * Loads users by username and maps HMS roles to Spring Security GrantedAuthorities.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        // Map HMS role (e.g. "PATIENT") to Spring Security authority (e.g. "ROLE_PATIENT")
        String role = user.getRole() != null ? user.getRole().toUpperCase() : "PATIENT";
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())   // already BCrypt-encoded in DB
                .authorities(authorities)
                .build();
    }
}
