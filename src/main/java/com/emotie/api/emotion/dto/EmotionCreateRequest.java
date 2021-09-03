package com.emotie.api.emotion.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
public class EmotionCreateRequest {

    private String emotion;
    private String color;

    @JsonCreator
    @Builder
    public EmotionCreateRequest(
                    @JsonProperty(value="emotion") String emotion,
                    @JsonProperty(value="color") String color
    ) {
        this.emotion = emotion;
        this.color = color;
    }
}
