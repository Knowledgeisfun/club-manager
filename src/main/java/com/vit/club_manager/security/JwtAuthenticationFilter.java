package com.vit.club_manager.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, CustomUserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    @SuppressWarnings("null")
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        // 1. Look for the "Authorization" header in the incoming HTTP request
        final String authorizationHeader = request.getHeader("Authorization");

        String email = null;
        String jwt = null;

        // 2. Check if the header exists and starts with "Bearer " (The industry standard for JWTs)
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            // Strip off the first 7 characters ("Bearer ") to get the raw token string
            jwt = authorizationHeader.substring(7);
            try {
                // Extract the email from the token
                email = jwtUtil.extractUsername(jwt);
            } catch (Exception e) {
                // If the token is expired or fake, this catches the error silently
                System.out.println("JWT Parsing failed: " + e.getMessage());
            }
        }

        // 3. If we found an email, and this user isn't already authenticated in this specific request...
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Load their database record
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(email);

            // 4. Validate the token's mathematical signature against the database record
            if (jwtUtil.validateToken(jwt)) {

                // 5. Create a "VIP Pass" (Authentication object) for Spring Security
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                
                usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                // 6. Slap the VIP Pass onto the Security Context. 
                // Now, when the request reaches the Controller, Spring knows exactly who they are!
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }
        
        // 7. Let the request continue on its journey to the Controller
        chain.doFilter(request, response);
    }
}