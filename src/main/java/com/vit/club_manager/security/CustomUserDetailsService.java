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
        
        Users user = usersRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        // Return our custom wrapper holding the entire user object!
        return new CustomUserDetails(user);
    }
}