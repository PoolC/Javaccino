package com.emotie.api.profile.dto;

import com.emotie.api.member.domain.Follow;
import lombok.Getter;

@Getter
public class FolloweeResponse {

    private String memberId;
    private String nickname;

    public FolloweeResponse(Follow follow){
        this.memberId = follow.getToMember().getUUID();
        this.nickname = follow.getToMember().getNickname();
    }
}
