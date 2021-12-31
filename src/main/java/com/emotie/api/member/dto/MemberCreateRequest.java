package com.emotie.api.member.dto;

import com.emotie.api.common.exception.NotSameException;
import com.emotie.api.member.domain.Gender;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Getter
public class MemberCreateRequest {
    @NotBlank(message = "닉네임을 입력해주세요.")
    @Size(message = "닉네임은 32글자보다 짧고, 적어도 1글자 이상이어야 합니다.", max = 32)
    private final String nickname;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=\\S+$).*$",
            message = "비밀번호는 적어도 하나의 영문 글자와 적어도 하나의 숫자를 포함해야 합니다. 띄어쓰기는 허용되지 않습니다.")
    @Size(message = "비밀번호는 8글자 이상 20글자 이하여야 합니다.", min = 8, max = 20)
    private final String password;

    @NotBlank(message = "비밀번호 확인란에 입력해주세요.")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=\\S+$).*$",
            message = "비밀번호는 적어도 하나의 영문 글자와 적어도 하나의 숫자를 포함해야 합니다. 띄어쓰기는 허용되지 않습니다.")
    @Size(message = "비밀번호는 8글자 이상 20글자 이하여야 합니다.", min = 8, max = 20)
    private final String passwordCheck;

    @NotNull(message = "성별을 선택해주세요.")
    private final Gender gender;

    @NotNull(message = "생년월일을 입력해주세요.")
    @PastOrPresent(message = "시간 여행자는 받아주지 않습니다.")
    private final LocalDate dateOfBirth;

    @NotBlank(message = "이메일을 입력해주세요.")
    @Email(message = "이메일 형식을 확인해 주세요.")
    private final String email;

    @JsonCreator
    @Builder
    public MemberCreateRequest(
            @JsonProperty(value = "nickname", required = true) String nickname,
            @JsonProperty(value = "password", required = true) String password,
            @JsonProperty(value = "passwordCheck", required = true) String passwordCheck,
            @JsonProperty(value = "gender", required = true) Gender gender,
            @JsonProperty(value = "dateOfBirth", required = true) LocalDate dateOfBirth,
            @JsonProperty(value = "email", required = true) String email
    ) {
        this.nickname = nickname;
        this.password = password;
        this.passwordCheck = passwordCheck;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.email = email;
    }

    public void checkPasswordMatches() {
        if (!this.password.equals(this.passwordCheck)) {
            throw new NotSameException("비밀번호와 비밀번호 확인 문자열이 다릅니다.");
        }
    }
}
