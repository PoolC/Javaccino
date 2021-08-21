package com.emotie.api.auth.dto;

import com.emotie.api.common.exception.NotSameException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import java.util.Optional;

@Getter
public class PasswordResetRequest {
    @NotBlank(message = "이메일을 입력해주세요.")
    private final String email;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    private final String password;

    @NotBlank(message = "비밀번호 확인란에 입력해주세요.")
    private final String passwordCheck;

    @JsonCreator
    @Builder
    public PasswordResetRequest(
            @JsonProperty("email") String email,
            @JsonProperty("password") String password,
            @JsonProperty("passwordCheck") String passwordCheck
    ) {
        this.email = email;
        this.password = password;
        this.passwordCheck = passwordCheck;
    }

    public void checkRequestValid() {
        Optional.ofNullable(password)
                .orElseThrow(() -> new IllegalArgumentException("입력값이 잘못되었습니다."));
        if (!password.equals(passwordCheck)) {
            throw new NotSameException("비밀번호와 비밃번호 체크가 틀렸습니다.");
        }
    }
}
