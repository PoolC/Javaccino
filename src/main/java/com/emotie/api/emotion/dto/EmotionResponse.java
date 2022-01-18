package com.emotie.api.emotion.dto;

import com.emotie.api.emotion.domain.Emotion;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class EmotionResponse {
    private String tag;
    private String color;

    public EmotionResponse(Emotion emotion) {
        this.tag = emotion.getName();
        this.color = emotion.getColor();
    }
    @JsonCreator
    public EmotionResponse(@JsonProperty("tag") String tag, @JsonProperty("color") String color) {
        this.tag = tag;
        this.color = color;
    }
}
