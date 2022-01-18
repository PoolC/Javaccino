package com.emotie.api.profile.dto;


import com.emotie.api.emotion.dto.EmotionResponse;
import com.emotie.api.emotion.dto.EmotionsResponse;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class ProfileResponse {

    private String nickname;
    private String introduction;
    private EmotionResponse allEmotion;
    private List<EmotionResponse> recentEmotion;
    private Boolean followed;
    private List<FollowerResponse> followers;
    private List<FolloweeResponse> followees;
    private String characterName;
    private String memberId;

    @JsonCreator
    @Builder
    public ProfileResponse(
            @JsonProperty("nickname") String nickname,
            @JsonProperty("introduction") String introduction,
            @JsonProperty("allEmotion") EmotionResponse allEmotion,
            @JsonProperty("recentEmotion") List<EmotionResponse> recentEmotion,
            @JsonProperty("followed") Boolean followed,
            @JsonProperty("followers") List<FollowerResponse> followers,
            @JsonProperty("followees") List<FolloweeResponse> followees,
            @JsonProperty("characterName") String characterName,
            @JsonProperty("memberId") String memberId) {

        this.nickname = nickname;
        this.introduction = introduction;
        this.allEmotion = allEmotion;
        this.recentEmotion = recentEmotion;
        this.followed = followed;
        this.followers = followers;
        this.followees = followees;
        this.characterName = characterName;
        this.memberId = memberId;
    }
}
