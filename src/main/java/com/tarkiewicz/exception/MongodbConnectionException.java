package com.tarkiewicz.exception;

public class MongodbConnectionException extends RuntimeException {

    private static final String message = "Unable to connect to the database, please report to the administrator";

    public MongodbConnectionException() {
        super(message);
    }
}
