package com.tarkiewicz.domain.account.service;

import com.tarkiewicz.domain.account.repository.AccountRepository;
import com.tarkiewicz.endpoint.dto.response.AccountResponse;
import com.tarkiewicz.endpoint.dto.request.RegisterRequestDto;
import jakarta.inject.Singleton;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Singleton
@AllArgsConstructor
@Slf4j
public class AccountService {

    private final AccountRepository accountRepository;

    public Mono<String> register(final RegisterRequestDto register) {
        return accountRepository.register(register.getUsername(), register.getPassword(), register.getEmail())
                .doOnNext(item -> log.info("Successfully register user with username: {}", register.getUsername()));
    }

    public Mono<AccountResponse> getUser(final String username) {
        return accountRepository.getUser(username)
                .doOnNext(accountResponse -> log.info("Successfully fetched user with username: {}", accountResponse.getUsername()));
    }
}
