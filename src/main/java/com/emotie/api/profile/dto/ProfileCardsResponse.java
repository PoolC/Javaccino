package com.emotie.api.profile.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
@Getter
public class ProfileCardsResponse {

    private final List<ProfileCardResponse> profiles;

    @JsonCreator
    @Builder
    public ProfileCardsResponse(@JsonProperty("profiles") List<ProfileCardResponse> profiles){
        this.profiles = profiles;
    }
}
