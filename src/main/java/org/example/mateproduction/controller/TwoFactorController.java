package org.example.mateproduction.controller;

import org.example.mateproduction.dto.response.TwoFactorResponse;
import org.example.mateproduction.service.TwoFactorService;
import org.example.mateproduction.service.impl.TwoFactorServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/2fa")
public class TwoFactorController {
    private final TwoFactorService twoFactorService;

    @Autowired
    public TwoFactorController(TwoFactorServiceImpl twoFactorService) {
        this.twoFactorService = twoFactorService;
    }

    @PostMapping("/generate")
    public ResponseEntity<TwoFactorResponse> generateQr(@RequestParam String email) {
        return ResponseEntity.ok(twoFactorService.generateQr(email));
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
