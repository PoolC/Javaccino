package com.emotie.api.emotion.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Getter
public class EmotionUpdateRequest {

    @NotEmpty(message = "감정이름을 입력해주세요.")
    private String emotion;

    @NotEmpty(message = "색깔을 입력해주세요")
    @Pattern(regexp = "^#(?:[0-9a-fA-F]{3,4}){1,2}$", message = "올바른 hex형태의 색깔을 입력해주세요.")
    private String color;

    @JsonCreator
    @Builder
    public EmotionUpdateRequest(
            @JsonProperty(value="emotion") String emotion,
            @JsonProperty(value="color") String color
    ) {
        this.emotion = emotion;
        this.color = color;
    }
}
