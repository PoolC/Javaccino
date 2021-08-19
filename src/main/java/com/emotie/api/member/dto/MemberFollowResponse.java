package com.emotie.api.member.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MemberFollowResponse {
    private Boolean isFollowing;

    @JsonCreator
    @Builder
    public MemberFollowResponse(
            @JsonProperty("isFollowing") Boolean isFollowing) {
        this.isFollowing = isFollowing;
    }
}
