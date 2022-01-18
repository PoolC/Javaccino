package com.emotie.api.profile.dto;

import com.emotie.api.member.domain.Follow;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
public class FolloweeResponse {

    private String memberId;
    private String nickname;

    public FolloweeResponse(Follow follow){
        this.memberId = follow.getToMember().getUUID();
        this.nickname = follow.getToMember().getNickname();
    }
    @JsonCreator
    @Builder
    public FolloweeResponse(@JsonProperty("memberId") String memberId, @JsonProperty("nickname") String nickname){
        this.memberId= memberId;
        this.nickname = nickname;
    }
}
