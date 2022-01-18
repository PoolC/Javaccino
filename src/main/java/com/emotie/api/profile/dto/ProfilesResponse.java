package com.emotie.api.profile.dto;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class ProfilesResponse {

    private final List<ProfileResponse> profiles;

    @JsonCreator
    @Builder
    public ProfilesResponse(@JsonProperty("profiles") List<ProfileResponse> profiles){
        this.profiles = profiles;
    }
}
