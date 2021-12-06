package com.tarkiewicz.exception.handler;

import com.tarkiewicz.endpoint.ErrorResponse;
import com.tarkiewicz.exception.MongodbConnectionException;
import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import jakarta.inject.Singleton;


@Singleton
@Produces
@Requires(classes = {MongodbConnectionException.class, ExceptionHandler.class})
public class MongodbConnectionExceptionHandler implements ExceptionHandler<MongodbConnectionException, HttpResponse<ErrorResponse>> {

    @Override
    public HttpResponse<ErrorResponse> handle(final HttpRequest request, MongodbConnectionException exception) {
        return HttpResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorResponse.of(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.getCode(), ""));

    }
}
