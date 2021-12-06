package com.tarkiewicz.repository.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RefreshTokenModel {

    @NotBlank
    private String username;

    @NotBlank
    private String refreshToken;

    @NotNull
    private Boolean revoked;

    @NotNull
    private Instant dateCreated;

    public static RefreshTokenModel of(final String username, final String refreshToken, final Boolean revoked, final Instant dateCreated) {
        return new RefreshTokenModel(username, refreshToken, revoked, dateCreated);
    }


}
