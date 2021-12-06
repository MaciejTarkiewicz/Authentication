package com.tarkiewicz.exception.handler;

import com.tarkiewicz.endpoint.ErrorResponse;
import com.tarkiewicz.exception.NotFoundException;
import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import jakarta.inject.Singleton;


@Singleton
@Produces
@Requires(classes = {NotFoundException.class, ExceptionHandler.class})
public class NotFoundExceptionHandler implements ExceptionHandler<NotFoundException, HttpResponse<ErrorResponse>> {

    @Override
    public HttpResponse<ErrorResponse> handle(final HttpRequest request, final NotFoundException exception) {
        return HttpResponse.status(HttpStatus.NOT_FOUND).body(ErrorResponse.of(exception.getMessage(), HttpStatus.NOT_FOUND.getCode(), ""));

    }
}
