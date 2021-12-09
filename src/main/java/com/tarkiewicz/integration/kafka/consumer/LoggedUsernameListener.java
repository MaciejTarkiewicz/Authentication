package com.tarkiewicz.integration.kafka.consumer;

import com.tarkiewicz.domain.account.repository.AccountRepository;
import io.micronaut.configuration.kafka.annotation.*;
import io.micronaut.core.annotation.Blocking;
import io.micronaut.messaging.Acknowledgement;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;

@KafkaListener(
        offsetReset = OffsetReset.EARLIEST,
        offsetStrategy = OffsetStrategy.DISABLED
)
@Slf4j
@AllArgsConstructor
public class LoggedUsernameListener {

    private final AccountRepository accountRepository;

    @Topic("logged-username-topic")
    @Blocking
    public Mono<Boolean> receiveUsername(final String username, final Acknowledgement acknowledgement) {
        return accountRepository.updateLastLoggedIn(username)
                .doOnTerminate(() -> log.info("Receive username {}", username))
                .doOnSuccess(handleSuccess(username, acknowledgement))
                .doOnError(handleError(username, acknowledgement));
    }

    private Consumer<Boolean> handleSuccess(final String username, final Acknowledgement acknowledgement) {
        return success -> {
            log.info("Successfully updated lastLoggedInField for user with username: {}", username);
            acknowledgement.ack();
        };
    }

    private Consumer<Throwable> handleError(final String username, final Acknowledgement acknowledgement) {
        return error -> {
            log.info("Cannot update lastLoggedInField for user with username: {}. Reason: {}", username, error.getMessage());
            acknowledgement.nack();
        };
    }
}
