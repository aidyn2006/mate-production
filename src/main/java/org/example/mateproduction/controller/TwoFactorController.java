package org.example.mateproduction.controller;

import org.example.mateproduction.config.Jwt.JwtUserDetails;
import org.example.mateproduction.dto.response.TwoFactorResponse;
import org.example.mateproduction.service.TwoFactorService;
import org.example.mateproduction.service.impl.TwoFactorServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/2fa")
public class TwoFactorController {
    private final TwoFactorService twoFactorService;

    @Autowired
    public TwoFactorController(TwoFactorServiceImpl twoFactorService) {
        this.twoFactorService = twoFactorService;
    }

    @PostMapping("/generate")
    public ResponseEntity<TwoFactorResponse> generateQr(@AuthenticationPrincipal JwtUserDetails userDetails) {
        // userDetails.getUsername() will give you the email of the logged-in user
        return ResponseEntity.ok(twoFactorService.generateQr(userDetails.getUsername()));
    }


    @PostMapping("/enable")
    public ResponseEntity<?> enableTwoFactor(@RequestParam String email,
                                             @RequestParam String secret,
                                             @RequestParam Integer code) {
        try {
            twoFactorService.enableTwoFactor(email, secret, code);
            return ResponseEntity.ok(Map.of("message", "2FA enabled successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Internal server error"));
        }
    }


}
