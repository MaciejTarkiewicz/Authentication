package com.tarkiewicz.endpoint.dto.response;

import lombok.Data;

@Data(staticConstructor = "of")
public class AccountResponse {

    private final String username;
    private final String email;
}
