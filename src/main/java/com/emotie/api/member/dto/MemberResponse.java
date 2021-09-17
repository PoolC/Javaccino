package com.emotie.api.member.dto;

import com.emotie.api.member.domain.Gender;
import com.emotie.api.member.domain.Member;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class MemberResponse {
    private final String nickname;
    private final Gender gender;
    private final LocalDate dateOfBirth;
    private final String email;
    private final String introduction;

    @JsonCreator
    @Builder
    public MemberResponse(@JsonProperty("name") String nickname, @JsonProperty("gender") Gender gender, @JsonProperty("dateOfBirth") LocalDate dateOfBirth, @JsonProperty("email") String email, @JsonProperty("introduction") String introduction) {
        this.nickname = nickname;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.email = email;
        this.introduction = introduction;
    }

    public static MemberResponse of(Member member) {
        return MemberResponse.builder()
                .nickname(member.getNickname())
                .gender(member.getGender())
                .dateOfBirth(member.getDateOfBirth())
                .email(member.getEmail())
                .introduction(member.getIntroduction())
                .build();
    }
}
