package com.tarkiewicz.endpoint.dto.response;

import lombok.Data;

@Data(staticConstructor = "of")
public class SuccessResponse {

    private final String message;

}
