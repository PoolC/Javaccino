package com.emotie.api.diary.dto;

import com.emotie.api.emotion.domain.Emotion;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;

@Deprecated
@Getter
public class DiaryUpdateRequest {
    @NotNull
    private final String emotion;

    @NotBlank
    private final String content;

    @NotNull
    private final Boolean isOpened;

    @Builder
    @JsonCreator
    public DiaryUpdateRequest(
            @JsonProperty(value = "emotion", required = true) String emotion,
            @JsonProperty(value = "content", required = true) String content,
            @JsonProperty(value = "is_opened", required = true) Boolean isOpened
    ) {
        this.emotion = emotion;
        this.content = content;
        this.isOpened = isOpened;
    }
}
