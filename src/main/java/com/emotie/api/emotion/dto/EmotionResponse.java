package com.emotie.api.emotion.dto;

import com.emotie.api.emotion.domain.Emotion;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmotionResponse {

    private Integer id;
    private String emotion;
    private String color;


    public EmotionResponse(Emotion emotion) {
        this.id = emotion.getId();
        this.emotion = emotion.getEmotion();
        this.color = emotion.getColor();
    }
    @JsonCreator
    public EmotionResponse(Integer id, String emotion, String color) {
        this.id = id;
        this.emotion = emotion;
        this.color = color;
    }

}
