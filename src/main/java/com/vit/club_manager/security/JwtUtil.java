package com.vit.club_manager.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String SECRET_KEY;
    
    @Value("${jwt.expiration}")
    private long JWT_EXPIRATION;

    // 1. CREATE THE TOKEN (The ID Badge)
    public String generateToken(UserDetails userDetails) {
        CustomUserDetails customUser = (CustomUserDetails) userDetails;
        
        // Normalize role name to ensure it is uppercase and has the 'ROLE_' prefix
        String rawRole = customUser.getUser().getRole().getRoleName();
        String normalizedRole = (rawRole != null) ? rawRole.toUpperCase() : "MEMBER";
        if (!normalizedRole.startsWith("ROLE_")) {
            normalizedRole = "ROLE_" + normalizedRole;
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put("role", normalizedRole);
        claims.put("teamId", customUser.getUser().getTeam() != null ? customUser.getUser().getTeam().getTeamId() : null);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(customUser.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // 2. READ THE TOKEN (Extract the user's email)
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // 3. VERIFY THE TOKEN
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // --- Helper Methods to parse the JWT payload ---

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}