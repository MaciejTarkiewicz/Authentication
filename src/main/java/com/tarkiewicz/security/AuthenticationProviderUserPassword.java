package com.tarkiewicz.security;

import com.tarkiewicz.repository.AccountRepository;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpRequest;
import io.micronaut.security.authentication.AuthenticationFailureReason;
import io.micronaut.security.authentication.AuthenticationProvider;
import io.micronaut.security.authentication.AuthenticationRequest;
import io.micronaut.security.authentication.AuthenticationResponse;
import jakarta.inject.Singleton;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

@Singleton
@AllArgsConstructor
@Slf4j
public class AuthenticationProviderUserPassword implements AuthenticationProvider {

    private final AccountRepository accountRepository;

    @Override
    public Publisher<AuthenticationResponse> authenticate(final @Nullable HttpRequest<?> httpRequest,
                                                          final AuthenticationRequest<?, ?> authenticationRequest) {

        return accountRepository.validCredentials((String) authenticationRequest.getIdentity(), (String) authenticationRequest.getSecret())
                .filter(BooleanUtils::isTrue)
                .doOnNext(success -> log.info("Successfully authentication user with username: {}", authenticationRequest.getIdentity()))
                .map(valid -> AuthenticationResponse.success((String) authenticationRequest.getIdentity()))
                .switchIfEmpty(Mono.error(AuthenticationResponse.exception(AuthenticationFailureReason.CREDENTIALS_DO_NOT_MATCH)));
    }
}
