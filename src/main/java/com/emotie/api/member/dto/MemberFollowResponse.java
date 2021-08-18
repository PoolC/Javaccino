package com.emotie.api.member.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class MemberFollowResponse {
    private Boolean isFollowing;

    @JsonCreator
    public MemberFollowResponse(
            @JsonProperty("isFollowing") Boolean isFollowing
    ) {
        this.isFollowing = isFollowing;
    }
}
