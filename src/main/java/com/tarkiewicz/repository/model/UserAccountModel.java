package com.tarkiewicz.repository.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserAccountModel {

    private String username;
    private String password;
    private String email;
    private Instant createdAt;

    public static UserAccountModel of(final String username, final String password, final String email, final Instant createdAt) {
        return new UserAccountModel(username, password, email, createdAt);
    }
}
