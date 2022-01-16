package com.emotie.api.profile.dto;


import com.emotie.api.emotion.dto.EmotionResponse;
import com.emotie.api.emotion.dto.EmotionsResponse;
import com.fasterxml.jackson.annotation.JsonCreator;
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

    @JsonCreator
    @Builder
    public ProfileResponse(String nickname, String introduction, EmotionResponse allEmotion, List<EmotionResponse> recentEmotion, Boolean followed, List<FollowerResponse> followers, List<FolloweeResponse> followees, String characterName) {
        this.nickname = nickname;
        this.introduction = introduction;
        this.allEmotion = allEmotion;
        this.recentEmotion = recentEmotion;
        this.followed = followed;
        this.followers = followers;
        this.followees = followees;
        this.characterName = characterName;
    }
}
