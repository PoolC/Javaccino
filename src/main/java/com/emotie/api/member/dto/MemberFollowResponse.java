package com.emotie.api.member.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MemberFollowResponse {
    private boolean isFollowing;

    @JsonCreator
    @Builder
    public MemberFollowResponse(
            @JsonProperty("isFollowing") boolean isFollowing) {
        this.isFollowing = isFollowing;
    }
}
