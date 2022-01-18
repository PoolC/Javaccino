package com.emotie.api.profile.dto;

import com.emotie.api.member.domain.Follow;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
public class FollowerResponse {

    private String memberId;
    private String nickname;

    public FollowerResponse(Follow follow){
        this.memberId= follow.getFromMember().getUUID();
        this.nickname = follow.getFromMember().getNickname();
    }

    @JsonCreator
    @Builder
    public FollowerResponse(@JsonProperty("memberId") String memberId, @JsonProperty("nickname") String nickname){
        this.memberId= memberId;
        this.nickname = nickname;
    }
}
