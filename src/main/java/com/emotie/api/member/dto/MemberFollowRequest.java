package com.emotie.api.member.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MemberFollowRequest {
    private final Boolean isFollowing;

    @JsonCreator
    @Builder
    public MemberFollowRequest(
            @JsonProperty("isFollowing") Boolean isFollowing
    ) {
        this.isFollowing = isFollowing;
    }
}
