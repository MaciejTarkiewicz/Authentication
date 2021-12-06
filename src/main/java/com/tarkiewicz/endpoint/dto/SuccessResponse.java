package com.tarkiewicz.endpoint.dto;

import lombok.Data;

@Data(staticConstructor = "of")
public class SuccessResponse {

    private final String message;

}
