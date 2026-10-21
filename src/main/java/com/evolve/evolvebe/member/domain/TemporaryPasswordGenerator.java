package com.evolve.evolvebe.member.domain;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class TemporaryPasswordGenerator {
    private static final String SYMBOLS =
            "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz23456789@#$%";
    private static final int LEN = 8;
    private final SecureRandom random = new SecureRandom();

    public String generate() {
        StringBuilder sb = new StringBuilder(LEN);
        for (int i = 0; i < LEN; i++) sb.append(SYMBOLS.charAt(random.nextInt(SYMBOLS.length())));
        return sb.toString();
    }

    public String generate(String customPassword) {
        if (customPassword == null || customPassword.isBlank()) {
            throw new IllegalArgumentException("customPassword must not be blank");
        }
        return customPassword;
    }
}
