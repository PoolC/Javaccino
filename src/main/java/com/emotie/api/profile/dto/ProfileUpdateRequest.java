package com.emotie.api.profile.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotNull;


@Getter
public class ProfileUpdateRequest {

    @NotNull
    private String introduction;

    @Builder
    @JsonCreator
    public ProfileUpdateRequest(
            @JsonProperty(value = "introduction", required = true) String introduction
    ) {
    this.introduction = introduction;
    }
}
