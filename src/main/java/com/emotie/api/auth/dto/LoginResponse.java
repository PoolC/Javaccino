package com.emotie.api.auth.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

@Getter
public class LoginResponse {
    private final String accessToken;

    @JsonCreator
    public LoginResponse(String accessToken) {
        this.accessToken = accessToken;
    }
}
