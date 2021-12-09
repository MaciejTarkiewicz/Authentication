package com.tarkiewicz.domain.account.repository.model;

import lombok.*;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class UserAccountModel {

    private String username;
    private String password;
    private String email;
    private Instant createdAt;
    private Instant lastLoggedIn;

    public static UserAccountModel of(final String username, final String password, final String email, final Instant createdAt) {
        return UserAccountModel.builder()
                .username(username)
                .password(password)
                .email(email)
                .createdAt(createdAt)
                .build();
    }
}
