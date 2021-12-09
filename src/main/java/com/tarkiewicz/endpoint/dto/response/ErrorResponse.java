package com.tarkiewicz.endpoint.dto.response;

import lombok.Data;

@Data(staticConstructor = "of")
public class ErrorResponse {

    private final String message;
    private final int code;
    private final String messageDetails;
}
