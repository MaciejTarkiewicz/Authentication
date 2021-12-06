package com.tarkiewicz.exception;

public class DuplicateKeyErrorException extends RuntimeException {

    private static final String message = "The user with username: %s already exist, please choose another username";

    public DuplicateKeyErrorException(final String username) {
        super(String.format(message, username));
    }
}
