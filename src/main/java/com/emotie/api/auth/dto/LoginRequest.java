package com.emotie.api.auth.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Builder;
import lombok.Getter;

@Getter
public class LoginRequest {
    private final String email;
    private final String password;

    @JsonCreator
    @Builder
    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
