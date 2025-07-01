package org.example.mateproduction.service.impl;


import org.example.mateproduction.dto.response.TwoFactorResponse;
import org.example.mateproduction.entity.User;
import org.example.mateproduction.repository.UserRepository;
import org.example.mateproduction.service.TwoFactorService;
import org.example.mateproduction.util.TwoFactorUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TwoFactorServiceImpl implements TwoFactorService {
    private final UserRepository userRepository;


    @Autowired
    public TwoFactorServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public TwoFactorResponse generateQr(String email){
        String secret = TwoFactorUtil.generateSecretKey();
        String qrUrl = TwoFactorUtil.getQrUrl(email, secret);
        return TwoFactorResponse.builder()
                .secret(secret)
                .qrUrl(qrUrl)
                .build();
    }

    public void enableTwoFactor(String email, String secret, Integer code) {
        boolean isValid = TwoFactorUtil.verifyCode(secret, code);
        if (!isValid) {
            throw new IllegalArgumentException("Invalid code");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getRole().name().equals("ADMIN")) {
            throw new SecurityException("Only ADMINs can enable 2FA");
        }

        user.setTwoFaSecret(secret);
        user.setIsTwoFaEnabled(true);
        userRepository.save(user);
    }

}
