package com.emotie.api.member.dto;

import com.emotie.api.member.domain.Gender;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;

@Getter
public class MemberUpdateRequest {
    @NotBlank(message = "닉네임을 입력해주세요.")
    private final String nickname;

    @NotNull(message = "성별을 선택해주세요.")
    private final Gender gender;

    @NotNull(message = "생년월일을 입력해주세요.")
    @PastOrPresent(message = "시간 여행자는 받아주지 않습니다.")
    private final LocalDate dateOfBirth;

    @JsonCreator
    @Builder
    public MemberUpdateRequest(
            @JsonProperty(value = "nickname", required = true) String nickname,
            @JsonProperty(value = "gender", required = true) Gender gender,
            @JsonProperty(value = "dateOfBirth", required = true) LocalDate dateOfBirth
    ) {
        this.nickname = nickname;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
    }
}
