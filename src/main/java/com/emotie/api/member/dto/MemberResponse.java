package com.emotie.api.member.dto;

import com.emotie.api.emotion.dto.EmotionResponse;
import com.emotie.api.member.domain.Gender;
import com.emotie.api.member.domain.Member;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class MemberResponse {
    private final String memberId;
    private final String nickname;
    private final Gender gender;
    private final LocalDate dateOfBirth;
    private final String email;
    private final String characterName;
    private final List<EmotionResponse> recentEmotion;

    @JsonCreator
    public MemberResponse(String memberId, String nickname, Gender gender, LocalDate dateOfBirth, String email, String characterName, List<EmotionResponse> recentEmotion) {
        this.memberId = memberId;
        this.nickname = nickname;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.email = email;
        this.characterName = characterName;
        this.recentEmotion = recentEmotion;
    }

    public MemberResponse(Member member, List<EmotionResponse> recentEmotion) {
        this.memberId = member.getUUID();
        this.nickname = member.getNickname();
        this.gender = member.getGender();
        this.dateOfBirth = member.getDateOfBirth();
        this.email = member.getEmail();
        this.characterName = member.getCharacterName();
        this.recentEmotion = recentEmotion;
    }
}
