package com.emotie.api.profile.dto;

import com.emotie.api.emotion.dto.EmotionResponse;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
@Getter
public class ProfileCardResponse {
    private String nickname;
    private String introduction;
    private EmotionResponse allEmotion;
    private List<EmotionResponse> recentEmotion;
    private String characterName;
    private String memberId;

    @JsonCreator
    @Builder
    public ProfileCardResponse(
            @JsonProperty("nickname") String nickname,
            @JsonProperty("introduction") String introduction,
            @JsonProperty("allEmotion") EmotionResponse allEmotion,
            @JsonProperty("recentEmotion") List<EmotionResponse> recentEmotion,
            @JsonProperty("characterName") String characterName,
            @JsonProperty("memberId") String memberId) {

        this.nickname = nickname;
        this.introduction = introduction;
        this.allEmotion = allEmotion;
        this.recentEmotion = recentEmotion;
        this.characterName = characterName;
        this.memberId = memberId;
    }
}
