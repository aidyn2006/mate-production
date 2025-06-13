package org.example.mateproduction.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.mateproduction.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.example.mateproduction.config.Jwt.JwtUserDetails;


@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository ur;

    @Override public UserDetails loadUserByUsername(String u){
        return ur.findByEmail(u)
                .map(JwtUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
