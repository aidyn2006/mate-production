package org.example.mateproduction.config.Jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsService uds;

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse resp, FilterChain fc)
            throws ServletException, IOException {
        String hdr = req.getHeader("Authorization");
        if (hdr == null || !hdr.startsWith("Bearer ")) {
            fc.doFilter(req, resp);
            return;
        }

        String token = hdr.substring(7);
        String usr = jwtService.extractUsername(token);
        if (usr != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails ud = uds.loadUserByUsername(usr);
            if (jwtService.isTokenValid(token, ud)) {
                UsernamePasswordAuthenticationToken at =
                        new UsernamePasswordAuthenticationToken(ud, null, ud.getAuthorities());
                at.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
                SecurityContextHolder.getContext().setAuthentication(at);
            }
        }
        fc.doFilter(req, resp);
    }


}

