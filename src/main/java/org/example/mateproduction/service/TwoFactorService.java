package org.example.mateproduction.service;

import org.example.mateproduction.dto.response.TwoFactorResponse;

public interface TwoFactorService {
    TwoFactorResponse generateQr(String email);
    void enableTwoFactor(String email, String secret, Integer code);
}
