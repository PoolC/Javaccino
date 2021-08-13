package com.emotie.api.member.dto;

import com.emotie.api.common.exception.NotSameException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class MemberCreateRequest {

    private final String nickname;
    private final String password;
    private final String passwordCheck;
    private final String gender;
    private final LocalDateTime dateOfBirth;
    private final String email;

    @JsonCreator
    @Builder
    public MemberCreateRequest(
            @JsonProperty("nickname") String nickname,
            @JsonProperty("password") String password,
            @JsonProperty("passwordCheck") String passwordCheck,
            @JsonProperty("gender") String gender,
            @JsonProperty("dateOfBirth") String dateOfBirth,
            @JsonProperty("email") String email
    ) {
        this.nickname = nickname;
        this.password = password;
        this.passwordCheck = passwordCheck;
        this.gender = gender;
        this.dateOfBirth = LocalDateTime.parse(dateOfBirth);
        this.email = email;
    }

    public void isPasswordMatches() {
        if (!this.password.equals(this.passwordCheck)) {
            throw new NotSameException("비밀번호와 비밀번호 확인 문자열이 다릅니다.");
        }
    }
}