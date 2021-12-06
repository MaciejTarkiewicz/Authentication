package com.tarkiewicz.endpoint;

import com.tarkiewicz.endpoint.dto.Account;
import com.tarkiewicz.endpoint.dto.RegisterDto;
import com.tarkiewicz.endpoint.dto.SuccessResponse;
import com.tarkiewicz.service.AccountService;
import io.micronaut.context.annotation.Parameter;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
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
    public Mono<HttpResponse<SuccessResponse>> register(@Valid @Body final RegisterDto registerDto) {
        return accountService.register(registerDto)
                .map(username -> HttpResponse.status(HttpStatus.CREATED)
                        .body(SuccessResponse.of(String.format("Successfully created account with username: %s", username))));
    }

    @Get("/get-user")
    @Secured(SecurityRule.IS_AUTHENTICATED)
    public Mono<HttpResponse<Account>> getAccountByUsername(@Parameter final String username) {
        return accountService.getUser(username)
                .map(account -> HttpResponse.status(HttpStatus.OK).body(account));
    }

}
