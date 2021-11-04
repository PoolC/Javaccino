package com.emotie.api.emotion.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import java.util.List;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmotionsResponse {

    private List<EmotionResponseUnused> emotions;

    @JsonCreator
    public EmotionsResponse(List<EmotionResponseUnused> emotions) { this.emotions = emotions; }
}
