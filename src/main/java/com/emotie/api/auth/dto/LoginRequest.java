package com.emotie.api.auth.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
public class LoginRequest {
    @NotNull(message = "이메일을 입력해주세요.")
    private final String email;

    @NotNull(message = "비밀번호를 입력해주세요.")
    private final String password;

    @JsonCreator
    @Builder
    public LoginRequest(
            @JsonProperty("email") String email,
            @JsonProperty("password") String password) {
        this.email = email;
        this.password = password;
    }
}
