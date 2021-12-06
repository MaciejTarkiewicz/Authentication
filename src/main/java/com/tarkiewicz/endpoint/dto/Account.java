package com.tarkiewicz.endpoint.dto;

import lombok.Data;

@Data(staticConstructor = "of")
public class Account {

    private final String username;
    private final String email;
}
