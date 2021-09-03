package com.emotie.api.emotion.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmotionResponse {

    private Integer id;
    private String emotion;
    private String color;

    @JsonCreator
    public EmotionResponse(Integer id, String emotion, String color) {
        this.id = id;
        this.emotion = emotion;
        this.color = color;
    }
}
