package org.example.mateproduction.controller;

import org.example.mateproduction.entity.User;
import org.example.mateproduction.exception.NotFoundException;
import org.example.mateproduction.repository.UserRepository;
import org.example.mateproduction.util.TwoFactorUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/2fa")
public class TwoFactorController {
    private final UserRepository userRepository;

    @Autowired
    public TwoFactorController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/generate")
    public ResponseEntity<Map<String, String>> generateQr(@RequestParam String email) {
        String secret = TwoFactorUtil.generateSecretKey();
        String qrUrl = TwoFactorUtil.getQrUrl(email, secret);

        return ResponseEntity.ok(Map.of(
                "secret", secret,
                "qrUrl", qrUrl
        ));
    }


    @PostMapping("/enable")
    public ResponseEntity<?> enableTwoFactor(@RequestParam String email,
                                             @RequestParam String secret,
                                             @RequestParam int code) {

        boolean isValid = TwoFactorUtil.verifyCode(secret, code);
        if (!isValid) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid code"));
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getRole().name().equals("ADMIN")) {
            return ResponseEntity.status(403).body(Map.of("error", "Only ADMINs can enable 2FA"));
        }

        user.setTwoFaSecret(secret);
        user.setIsTwoFaEnabled(true);
        userRepository.save(user);

        return ResponseEntity.ok(Map.of("message", "2FA enabled successfully"));
    }

}
