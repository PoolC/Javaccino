package com.emotie.api.recommend.dto;

import com.emotie.api.profile.dto.ProfileResponse;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class RecommendResponse {
    private final List<ProfileResponse> profiles;

    @JsonCreator
    @Builder
    public RecommendResponse(
            List<ProfileResponse> profiles
    ) {
        this.profiles = profiles;
    }
}
