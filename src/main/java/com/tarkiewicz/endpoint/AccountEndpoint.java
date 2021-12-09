package com.tarkiewicz.endpoint;

import com.tarkiewicz.endpoint.dto.response.AccountResponse;
import com.tarkiewicz.endpoint.dto.request.RegisterRequestDto;
import com.tarkiewicz.endpoint.dto.response.SuccessResponse;
import com.tarkiewicz.domain.account.service.AccountService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.*;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@Controller("/account")
@AllArgsConstructor
public class AccountEndpoint {

    private final AccountService accountService;

    @Post("/register")
    @Secured(SecurityRule.IS_ANONYMOUS)
    public Mono<HttpResponse<SuccessResponse>> register(@Valid @Body final RegisterRequestDto registerRequestDto) {
        return accountService.register(registerRequestDto)
                .map(username -> HttpResponse.status(HttpStatus.CREATED)
                        .body(SuccessResponse.of(String.format("Successfully created account with username: %s", username))));
    }

    @Get("/get-user")
    @Secured(SecurityRule.IS_AUTHENTICATED)
    public Mono<HttpResponse<AccountResponse>> getAccountByUsername(@QueryValue("username") final String username) {
        return accountService.getUser(username)
                .map(accountResponse -> HttpResponse.status(HttpStatus.OK).body(accountResponse));
    }
}
