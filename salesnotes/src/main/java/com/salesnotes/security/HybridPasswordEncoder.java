package com.salesnotes.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;
import java.util.regex.Pattern;

/**
 * HybridPasswordEncoder:
 * - If the password is encrypted (BCrypt hash, AES cipher, or Base64), decrypts/verifies it.
 * - Otherwise, verifies the password as a direct text pattern (plain text match).
 */
public class HybridPasswordEncoder implements PasswordEncoder {

    private static final Logger log = LoggerFactory.getLogger(HybridPasswordEncoder.class);

    private final BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();

    // Standard BCrypt regex pattern ($2a$, $2b$, $2y$, $2x$)
    private static final Pattern BCRYPT_PATTERN = Pattern.compile("^\\$2[abyx]\\$\\d{2}\\$[./A-Za-z0-9]{53}$");

    // Standard AES secret key derived from seed
    private static final String AES_SECRET_SEED = "salesnotes-secure-encryption-key-2026";

    @Override
    public String encode(CharSequence rawPassword) {
        if (rawPassword == null) {
            return null;
        }
        return bcrypt.encode(rawPassword);
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        if (rawPassword == null || encodedPassword == null) {
            return false;
        }

        String raw = rawPassword.toString();
        String stored = encodedPassword.trim();

        // Check if raw presentation itself is AES-encrypted (e.g. ENC(...) or {aes}...)
        if (isAesEncrypted(raw)) {
            String decryptedRaw = decryptAes(raw);
            if (decryptedRaw != null) {
                raw = decryptedRaw;
            }
        }

        // 1. If stored password is encrypted with BCrypt -> verify using BCrypt matcher
        if (isBCrypt(stored)) {
            try {
                if (bcrypt.matches(raw, stored)) {
                    return true;
                }
            } catch (Exception e) {
                log.debug("BCrypt match failed with exception: {}", e.getMessage());
            }
        }

        // 2. If stored password is encrypted with AES -> decrypt and compare
        if (isAesEncrypted(stored)) {
            try {
                String decryptedStored = decryptAes(stored);
                if (decryptedStored != null && raw.equals(decryptedStored)) {
                    return true;
                }
            } catch (Exception e) {
                log.debug("AES decryption match failed with exception: {}", e.getMessage());
            }
        }

        // 3. If stored password is Base64 encoded -> decode and compare
        if (isBase64(stored)) {
            try {
                byte[] decodedBytes = Base64.getDecoder().decode(stored);
                String decodedStored = new String(decodedBytes, StandardCharsets.UTF_8);
                if (raw.equals(decodedStored)) {
                    return true;
                }
            } catch (Exception ignored) {
            }
        }

        // 4. Verify as plain text pattern match
        if (raw.equals(stored)) {
            return true;
        }

        return false;
    }

    /**
     * Checks if the password string is encrypted/hashed.
     */
    public boolean isEncrypted(String password) {
        if (password == null) {
            return false;
        }
        return isBCrypt(password) || isAesEncrypted(password) || password.startsWith("{");
    }

    /**
     * Checks if the password string matches BCrypt hash format.
     */
    public boolean isBCrypt(String password) {
        if (password == null) {
            return false;
        }
        return password.startsWith("$2a$") ||
               password.startsWith("$2b$") ||
               password.startsWith("$2y$") ||
               password.startsWith("$2x$") ||
               BCRYPT_PATTERN.matcher(password).matches();
    }

    /**
     * Checks if the password string is AES-encrypted.
     */
    public boolean isAesEncrypted(String password) {
        if (password == null) {
            return false;
        }
        return (password.startsWith("ENC(") && password.endsWith(")")) ||
               password.startsWith("{aes}") ||
               password.startsWith("{cipher}");
    }

    /**
     * Decrypts an AES encrypted password string.
     */
    public String decryptAes(String cipherText) {
        if (cipherText == null) {
            return null;
        }
        try {
            String payload = cipherText.trim();
            if (payload.startsWith("ENC(") && payload.endsWith(")")) {
                payload = payload.substring(4, payload.length() - 1);
            } else if (payload.startsWith("{aes}")) {
                payload = payload.substring(5);
            } else if (payload.startsWith("{cipher}")) {
                payload = payload.substring(8);
            }

            SecretKeySpec secretKey = deriveAesKey(AES_SECRET_SEED);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(payload));
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Encrypts a plain text password with AES (for optional reversible encryption).
     */
    public String encryptAes(String plainText) {
        if (plainText == null) {
            return null;
        }
        try {
            SecretKeySpec secretKey = deriveAesKey(AES_SECRET_SEED);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            return "ENC(" + Base64.getEncoder().encodeToString(encryptedBytes) + ")";
        } catch (Exception e) {
            throw new RuntimeException("AES encryption error", e);
        }
    }

    private boolean isBase64(String str) {
        if (str == null || str.length() < 8 || str.length() % 4 != 0) {
            return false;
        }
        try {
            byte[] decoded = Base64.getDecoder().decode(str);
            String reEncoded = Base64.getEncoder().encodeToString(decoded);
            return str.equals(reEncoded);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private SecretKeySpec deriveAesKey(String seed) throws Exception {
        byte[] keyBytes = MessageDigest.getInstance("SHA-256")
                .digest(seed.getBytes(StandardCharsets.UTF_8));
        return new SecretKeySpec(Arrays.copyOf(keyBytes, 16), "AES");
    }
}
