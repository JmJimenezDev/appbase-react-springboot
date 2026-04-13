package dev.jmjimenez.appbase_rest.security.util;

import java.security.SecureRandom;
import java.util.Base64;

public final class CsrfTokenUtil {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final Base64.Encoder BASE64_ENCODER = Base64.getUrlEncoder().withoutPadding();

    private CsrfTokenUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static String generateCsrfToken() {
        byte[] randomBytes = new byte[32]; // 256 bits
        SECURE_RANDOM.nextBytes(randomBytes);
        return BASE64_ENCODER.encodeToString(randomBytes);
    }
}
