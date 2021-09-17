package com.emotie.api.emotion.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import java.util.List;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmotionsResponse {

    private List<EmotionResponse> emotions;

    @JsonCreator
    public EmotionsResponse(List<EmotionResponse> emotions) { this.emotions = emotions; }
}
