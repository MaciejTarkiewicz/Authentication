package com.tarkiewicz.exception.handler;

import com.tarkiewicz.endpoint.ErrorResponse;
import com.tarkiewicz.exception.DuplicateKeyErrorException;
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
@Requires(classes = {DuplicateKeyErrorException.class, ExceptionHandler.class})
public class DuplicateKeyErrorExceptionHandler implements ExceptionHandler<DuplicateKeyErrorException, HttpResponse<ErrorResponse>> {

    @Override
    public HttpResponse<ErrorResponse> handle(final HttpRequest request, final DuplicateKeyErrorException exception) {
        return HttpResponse.status(HttpStatus.CONFLICT).body(ErrorResponse.of(exception.getMessage(), HttpStatus.CONFLICT.getCode(), ""));

    }
}
