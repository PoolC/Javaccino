package com.emotie.api.auth.infra;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PasswordHashProvider {
    private final PasswordEncoder encoder;

    public String encodePassword(String rawPassword) {
        return encoder.encode(rawPassword);
    }

    public boolean matches(String rawPassword, String hashPassword) {
        return encoder.matches(rawPassword, hashPassword);
    }
}
