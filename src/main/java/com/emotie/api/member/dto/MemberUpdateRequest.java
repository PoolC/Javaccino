package com.emotie.api.member.dto;

import com.emotie.api.common.exception.NotSameException;
import com.emotie.api.member.domain.Gender;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import java.time.LocalDate;

@Getter
public class MemberUpdateRequest {
    @NotBlank(message = "비밀번호를 입력해주세요.")
    private final String password;

    @NotBlank(message = "비밀번호 확인란에 입력해주세요.")
    private final String passwordCheck;

    @NotBlank(message = "성별을 선택해주세요.")
    private final Gender gender;

    @NotBlank(message = "생년월일을 입력해주세요.")
    @Past(message = "시간 여행자는 받아주지 않습니다.")
    private final LocalDate dateOfBirth;

    @JsonCreator
    @Builder
    public MemberUpdateRequest(
            @JsonProperty("password") String password,
            @JsonProperty("passwordCheck") String passwordCheck,
            @JsonProperty("gender") Gender gender,
            @JsonProperty("dateOfBirth") LocalDate dateOfBirth
    ) {
        this.password = password;
        this.passwordCheck = passwordCheck;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
    }

    public void checkPasswordMatches() {
        if (!this.password.equals(this.passwordCheck)) {
            throw new NotSameException("비밀번호와 비밀번호 확인 문자열이 다릅니다.");
        }
    }
}
