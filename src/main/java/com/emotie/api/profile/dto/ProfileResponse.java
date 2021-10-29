package com.emotie.api.profile.dto;


import com.emotie.api.emotion.dto.EmotionResponse;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class ProfileResponse {

    private String nickname;
    private String introduction;
    private List<EmotionResponse> allEmotion;
    private List<EmotionResponse> recentEmotion;
    private Boolean followed;
    private List<FollowerResponse> followers;
    private List<FolloweeResponse> followees;

    @JsonCreator
    @Builder
    public ProfileResponse(String nickname, String introduction, List<EmotionResponse> allEmotion, List<EmotionResponse> recentEmotion, Boolean followed, List<FollowerResponse> followers, List<FolloweeResponse> followees) {
        this.nickname = nickname;
        this.introduction = introduction;
        this.allEmotion = allEmotion;
        this.recentEmotion = recentEmotion;
        this.followed = followed;
        this.followers = followers;
        this.followees = followees;
    }

}
