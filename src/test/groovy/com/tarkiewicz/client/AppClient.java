package com.tarkiewicz.client;

import com.tarkiewicz.endpoint.dto.request.RegisterRequestDto;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.security.authentication.UsernamePasswordCredentials;
import io.micronaut.security.token.jwt.endpoints.TokenRefreshRequest;
import io.micronaut.security.token.jwt.render.AccessRefreshToken;
import io.micronaut.security.token.jwt.render.BearerAccessRefreshToken;

import java.util.Map;

@Client("/")
public interface AppClient {

    @Post("/login")
    BearerAccessRefreshToken login(final @Body UsernamePasswordCredentials credentials);

    @Post("/oauth/access_token")
    AccessRefreshToken refreshToken(final @Body TokenRefreshRequest tokenRefreshRequest);

    @Post("/account/register")
    HttpResponse<Map<String, String>> register(final @Body RegisterRequestDto registerRequestDto);

    @Get("/account/get-user")
    HttpResponse<Map<String, Object>> getUser(@QueryValue("username") final String username, @Header("Authorization") final String token);

}
