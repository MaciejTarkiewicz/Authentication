package com.tarkiewicz.service;

import com.tarkiewicz.endpoint.dto.Account;
import com.tarkiewicz.endpoint.dto.RegisterDto;
import com.tarkiewicz.repository.AccountRepository;
import jakarta.inject.Singleton;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Singleton
@AllArgsConstructor
@Slf4j
public class AccountService {

    private final AccountRepository accountRepository;

    public Mono<String> register(final RegisterDto register) {
        return accountRepository.register(register.getUsername(), register.getPassword(), register.getEmail())
                .doOnNext(item -> log.info("Successfully register user with username: {}", register.getUsername()));
    }

    public Mono<Account> getUser(final String username) {
        return accountRepository.getUser(username)
                .doOnNext(account -> log.info("Successfully fetched user with username: {}", account.getUsername()));
    }
}
