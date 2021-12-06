package com.tarkiewicz.security;

import com.tarkiewicz.repository.RefreshTokenRepository;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.errors.OauthErrorResponseException;
import io.micronaut.security.token.event.RefreshTokenGeneratedEvent;
import io.micronaut.security.token.refresh.RefreshTokenPersistence;
import jakarta.inject.Singleton;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

import static io.micronaut.security.errors.IssuingAnAccessTokenErrorCode.INVALID_GRANT;

@Singleton
@AllArgsConstructor
@Slf4j
public class CustomRefreshTokenPersistence implements RefreshTokenPersistence {

    private static final String REFRESH_TOKEN_NOT_FOUND = "refresh token not found";
    private static final String REFRESH_TOKEN_REVOKED = "refresh token revoked";

    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public void persistToken(RefreshTokenGeneratedEvent event) {
        if (event != null &&
                event.getRefreshToken() != null &&
                event.getAuthentication() != null &&
                event.getAuthentication().getName() != null) {
            refreshTokenRepository.save(event.getAuthentication().getName(), event.getRefreshToken(), Boolean.FALSE)
                    .subscribe(success -> log.info("Successfully saved refreshToken"),
                            err -> log.error("Cannot save refreshToken! Reason: {}", err.getMessage()));
        }
    }

    @Override
    public Publisher<Authentication> getAuthentication(final String refreshToken) {
        return refreshTokenRepository.findByRefreshToken(refreshToken)
                .switchIfEmpty(Mono.error(new OauthErrorResponseException(INVALID_GRANT, REFRESH_TOKEN_NOT_FOUND, null)))
                .filter(token -> !token.getRevoked())
                .switchIfEmpty(Mono.error(new OauthErrorResponseException(INVALID_GRANT, REFRESH_TOKEN_REVOKED, null)))
                .doOnNext(token -> log.info("Successfully generated access token for user with username: {}", token.getUsername()))
                .map(token -> Authentication.build(token.getUsername()));
    }
}
