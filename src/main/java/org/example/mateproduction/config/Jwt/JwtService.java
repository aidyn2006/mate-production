package org.example.mateproduction.config.Jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;

@Service
public class JwtService {

    private final String SECRET = "TOBEHONESTIAMSOTIREDBUTCNATYASHTATANDTHISVALIDATIONSISBADICANSAYOTHERWORDS";

    @Autowired
    private UserDetailsService userDetailsService;

    public String generateToken(UserDetails ud) {
        return Jwts.builder()
                .setSubject(ud.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(Date.from(LocalDateTime.now().plusHours(5)
                        .atZone(ZoneId.systemDefault()).toInstant()))
                .signWith(SignatureAlgorithm.HS256, SECRET)
                .compact();
    }

    public String extractUsername(String token) {
        return Jwts.parser().setSigningKey(SECRET)
                .parseClaimsJws(token).getBody().getSubject();
    }

    public boolean isTokenValid(String token, UserDetails ud) {
        return extractUsername(token).equals(ud.getUsername()) &&
                !Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token)
                        .getBody().getExpiration().before(new Date());
    }

    // src/main/java/org/example/mateproduction/config/Jwt/JwtService.java
// (Likely already correct, but double-check this part)

    public UsernamePasswordAuthenticationToken getAuthentication(String token) {
        String username = extractUsername(token); // Assumes this extracts correctly
        if (username == null) {
            throw new IllegalArgumentException("Username not found in token");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        if (userDetails == null) {
            throw new IllegalArgumentException("User details not found for username: " + username);
        }

        if (isTokenValid(token, userDetails)) {
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, // The principal (UserDetail object)
                    null,        // Credentials (often null for JWT)
                    userDetails.getAuthorities() // User's authorities
            );
            // NO NEED TO CALL .setAuthenticated(true) here explicitly, constructor does it if authorities are non-null.
            // However, if your UserDetails has no authorities, or authorities are empty, it might be unauthenticated by default.
            // Ensure userDetails.getAuthorities() returns at least an empty list, not null.

            return authentication;
        } else {
            throw new IllegalArgumentException("Invalid JWT token for user: " + username);
        }
    }
}

