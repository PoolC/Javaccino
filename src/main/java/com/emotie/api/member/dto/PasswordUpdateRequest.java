package com.emotie.api.member.dto;

import com.emotie.api.common.exception.NotSameException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
public class PasswordUpdateRequest {
    @NotBlank(message = "현재 비밀번호를 입력해주세요.")
    private final String currentPassword;

    @NotBlank(message = "변경할 비밀번호를 입력해주세요.")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=\\S+$).*$",
            message = "비밀번호는 적어도 하나의 영문 글자와 적어도 하나의 숫자를 포함해야 합니다. 띄어쓰기는 허용되지 않습니다.")
    @Size(message = "비밀번호는 8글자 이상 20글자 이하여야 합니다.", min = 8, max = 20)
    private final String password;

    @NotBlank(message = "비밀번호 확인란에 입력해주세요.")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=\\S+$).*$",
            message = "비밀번호는 적어도 하나의 영문 글자와 적어도 하나의 숫자를 포함해야 합니다. 띄어쓰기는 허용되지 않습니다.")
    @Size(message = "비밀번호는 8글자 이상 20글자 이하여야 합니다.", min = 8, max = 20)
    private final String passwordCheck;

    @JsonCreator
    @Builder
    public PasswordUpdateRequest(
            @JsonProperty(value = "currentPassword", required = true) String currentPassword,
            @JsonProperty(value = "password", required = true) String password,
            @JsonProperty(value = "passwordCheck", required = true) String passwordCheck
    ) {
        this.currentPassword = currentPassword;
        this.password = password;
        this.passwordCheck = passwordCheck;
    }

    public void checkPasswordMatches() {
        if (!this.password.equals(this.passwordCheck)) {
            throw new NotSameException("비밀번호와 비밀번호 확인 문자열이 다릅니다.");
        }
    }
}
