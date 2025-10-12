package com.synclearn.security;

import org.springframework.security.crypto.password.PasswordEncoder;

public class UserSecurityAdapter implements SecurityRepository {
    private final PasswordEncoder passwordEncoder;

    public UserSecurityAdapter(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public String pwEncrypt(String password) {
        return passwordEncoder.encode(password);
    }
}
