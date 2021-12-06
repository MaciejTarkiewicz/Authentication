package com.tarkiewicz.endpoint;

import lombok.Data;

@Data(staticConstructor = "of")
public class ErrorResponse {

    private final String message;
    private final int code;
    private final String messageDetails;
}
