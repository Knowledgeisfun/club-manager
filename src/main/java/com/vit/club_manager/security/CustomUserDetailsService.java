package com.vit.club_manager.security;

import com.vit.club_manager.model.Users;
import com.vit.club_manager.repository.UsersRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service // Tells Spring to register this as a core business component
public class CustomUserDetailsService implements UserDetailsService {

    private final UsersRepository usersRepository;

    public CustomUserDetailsService(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    // Spring Security calls this method automatically during login
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        
        // 1. Find the user in our MySQL database
        Users user = usersRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        // 2. Translate our custom Role into a Spring Security "Authority"
        // VERY IMPORTANT: Spring Security requires roles to start with the prefix "ROLE_"
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + user.getRole().getRoleName().toUpperCase());

        // 3. Return Spring Security's built-in User object
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),          // The username they log in with
                user.getPasswordHash(),       // The BCrypt hashed password from the DB
                Collections.singletonList(authority) // Their permissions
        );
    }
}