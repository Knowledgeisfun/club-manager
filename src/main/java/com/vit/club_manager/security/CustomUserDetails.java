package com.vit.club_manager.security;

import com.vit.club_manager.model.Users;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class CustomUserDetails implements UserDetails {

    // The secret payload: our full database entity!
    private final Users user;

    public CustomUserDetails(Users user) {
        this.user = user;
    }

    // A custom getter so we can extract our user later
    public Users getUser() {
        return user;
    }

    // --- Spring Security Required Methods Below ---

   @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String roleName = user.getRole().getRoleName().toUpperCase();
        
        // Only add "ROLE_" if the database string doesn't already have it
        if (!roleName.startsWith("ROLE_")) {
            roleName = "ROLE_" + roleName;
        }
        
        return Collections.singletonList(new SimpleGrantedAuthority(roleName));
    }

    @Override
    public String getPassword() {
        return user.getPasswordHash();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}