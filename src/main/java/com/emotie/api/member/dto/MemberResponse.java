package com.emotie.api.member.dto;

import com.emotie.api.member.domain.Gender;
import com.emotie.api.member.domain.Member;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class MemberResponse {
    private final String memberId;
    private final String nickname;
    private final Gender gender;
    private final LocalDate dateOfBirth;
    private final String email;

    @JsonCreator
    public MemberResponse(String memberId, String nickname, Gender gender, LocalDate dateOfBirth, String email) {
        this.memberId = memberId;
        this.nickname = nickname;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.email = email;
    }

    public MemberResponse(Member member) {
        this.memberId = member.getUUID();
        this.nickname = member.getNickname();
        this.gender = member.getGender();
        this.dateOfBirth = member.getDateOfBirth();
        this.email = member.getEmail();
    }
}
