package com.qingyou.sso.utils;

import lombok.SneakyThrows;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordEncodeUtils {

    @SneakyThrows
    public static EncodedPassword encode(String password) {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 256);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] bytes = factory.generateSecret(spec).getEncoded();
        return new EncodedPassword(Base64.getEncoder().encodeToString(bytes), Base64.getEncoder().encodeToString(salt));
    }

    @SneakyThrows
    public static EncodedPassword encode(String password, String salt) {
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), Base64.getDecoder().decode(salt), 65536, 256);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] bytes = factory.generateSecret(spec).getEncoded();
        return new EncodedPassword(Base64.getEncoder().encodeToString(bytes), salt);
    }

    public record EncodedPassword(String encoded, String salt) {
    }


}
