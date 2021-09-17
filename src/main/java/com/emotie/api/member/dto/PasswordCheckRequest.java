package com.emotie.api.member.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
public class PasswordCheckRequest {
    @NotBlank(message = "비밀번호를 입력해주세요.")
    private String password;

    @JsonCreator
    @Builder
    public PasswordCheckRequest(@JsonProperty(value = "password", required = true) String password) {
        this.password = password;
    }
}
