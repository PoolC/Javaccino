package com.emotie.api.profile.dto;


import com.emotie.api.emotion.dto.EmotionResponse;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ProfileResponse {

    String nickname;
    String introduction;
    List<EmotionResponse> allEmotion = new ArrayList<>();
    List<EmotionResponse> recentEmotion = new ArrayList<>();
    Boolean followed;
    List<FollowerResponse> followers = new ArrayList<>();
    List<FolloweeResponse> followees = new ArrayList<>();


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
