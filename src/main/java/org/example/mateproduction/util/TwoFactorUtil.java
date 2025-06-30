package org.example.mateproduction.util;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;

public class TwoFactorUtil {

    public static String generateSecretKey() {
        GoogleAuthenticator gAuth = new GoogleAuthenticator();
        GoogleAuthenticatorKey key = gAuth.createCredentials();
        return key.getKey();
    }

    public static String getQrUrl(String userEmail, String secretKey) {
        String otpAuthUrl = String.format(
                "otpauth://totp/%s:%s?secret=%s&issuer=%s",
                "MateProductionApp", userEmail, secretKey, "MateProductionApp"
        );
        return "https://api.qrserver.com/v1/create-qr-code/?data=" + java.net.URLEncoder.encode(otpAuthUrl, java.nio.charset.StandardCharsets.UTF_8) + "&size=200x200";
    }


    public static boolean verifyCode(String secret, int code) {
        GoogleAuthenticator gAuth = new GoogleAuthenticator();
        return gAuth.authorize(secret, code);
    }
}
