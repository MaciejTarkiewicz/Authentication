package com.tarkiewicz.client;

import com.tarkiewicz.endpoint.dto.RegisterDto;
import io.micronaut.context.annotation.Parameter;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Header;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.security.authentication.UsernamePasswordCredentials;
import io.micronaut.security.token.jwt.render.BearerAccessRefreshToken;

import java.util.Map;

@Client("/")
public interface AppClient {

    @Post("/login")
    BearerAccessRefreshToken login(final @Body UsernamePasswordCredentials credentials);

    @Post("/account/register")
    HttpResponse<Map<String, String>> register(final @Body RegisterDto registerDto);

    @Get("/account/get-user")
    Map<String, Object> getUser(@Parameter final String username, @Header("Authorization") final String token);

}
