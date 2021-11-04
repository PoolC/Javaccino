package com.emotie.api.diary.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class DiaryCreateRequest {
    @NotNull
    private final String emotion;

    @NotBlank
    private final String content;

    @NotNull
    private final Boolean isOpened;

    @Builder
    @JsonCreator
    public DiaryCreateRequest(
            @JsonProperty(value = "emotion", required = true) String emotion,
            @JsonProperty(value = "content", required = true) String content,
            @JsonProperty(value = "isOpened", required = true) Boolean isOpened
    ) {
        this.emotion = emotion;
        this.content = content;
        this.isOpened = isOpened;
    }
}
