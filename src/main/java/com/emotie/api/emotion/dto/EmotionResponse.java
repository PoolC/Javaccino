package com.emotie.api.emotion.dto;

import com.emotie.api.emotion.domain.Emotion;
import lombok.Getter;

@Getter
public class EmotionResponse {
    private String tag;
    private String color;

    public EmotionResponse(Emotion emotion) {
        this.tag = emotion.getEmotion();
        this.color = emotion.getColor();
    }
}
