package com.emotie.api.member.dto;

import com.emotie.api.common.exception.NotSameException;
import com.emotie.api.member.domain.Gender;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import org.springframework.lang.Nullable;

import java.time.LocalDate;

@Getter
public class MemberUpdateRequest {
    private final String password;
    private final String passwordCheck;
    private final String gender;
    private final String dateOfBirth;

    @JsonCreator
    @Builder
    public MemberUpdateRequest(
            @JsonProperty("password") String password,
            @JsonProperty("passwordCheck") String passwordCheck,
            @JsonProperty("gender") String gender,
            @JsonProperty("dateOfBirth") String dateOfBirth
    ) {
        this.password = password;
        this.passwordCheck = passwordCheck;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
    }

    private void checkPasswordMatches() {
        if (!this.password.equals(this.passwordCheck)) {
            throw new NotSameException("비밀번호와 비밀번호 확인 문자열이 다릅니다.");
        }
    }

    private void checkPasswordIsEmptyNotNull() {
        // null이 아닌데도 불구하고, 빈 문자열
        if ((this.password != null) && (this.password.isBlank())){
            throw new IllegalArgumentException("비밀번호는 공백으로만 이루어질 수 없습니다.");
        }
    }

    public void checkPasswordValidity() {
        if (this.password != null) {
            checkPasswordIsEmptyNotNull();
            checkPasswordMatches();
        } else if (passwordCheck != null) {
            throw new IllegalArgumentException("비밀번호 없이 비밀번호 확인 문자열만 제공되었습니다.");
        }
    }
}
