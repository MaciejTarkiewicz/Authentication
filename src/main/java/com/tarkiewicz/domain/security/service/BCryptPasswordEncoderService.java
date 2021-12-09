package com.tarkiewicz.domain.security.service;

import io.micronaut.core.annotation.NonNull;
import jakarta.inject.Singleton;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.validation.constraints.NotBlank;

@Singleton
public class BCryptPasswordEncoderService implements PasswordEncoder {

    private final PasswordEncoder delegate = new BCryptPasswordEncoder();

    @Override
    public String encode(@NotBlank @NonNull final CharSequence rawPassword) {
        return delegate.encode(rawPassword);
    }

    @Override
    public boolean matches(@NotBlank @NonNull final CharSequence rawPassword,
                           @NotBlank @NonNull final String encodedPassword) {
        return delegate.matches(rawPassword, encodedPassword);
    }
}