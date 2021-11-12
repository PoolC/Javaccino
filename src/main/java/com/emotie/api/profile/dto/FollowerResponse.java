package com.emotie.api.profile.dto;

import com.emotie.api.member.domain.Follow;
import lombok.Getter;

@Getter
public class FollowerResponse {

    private String memberId;
    private String nickname;

    public FollowerResponse(Follow follow){
        this.memberId= follow.getFromMember().getUUID();
        this.nickname = follow.getFromMember().getNickname();
    }
}
