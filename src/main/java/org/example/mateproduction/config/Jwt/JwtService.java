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

    public Authentication getAuthentication(String token) {
        String username = extractUsername(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
    }
}

