package com.emotie.api.diary.dto;

import com.emotie.api.emotion.domain.Emotion;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class DiaryCreateRequest {
    @NotNull
    @PastOrPresent(message = "죄송합니다. 시간 여행을 기록하실 수는 없습니다.")
    private final LocalDate issuedDate;

    @NotNull
    private final String emotion;

    @NotBlank
    private final String content;

    @NotNull
    private final Boolean isOpened;

    @Builder
    @JsonCreator
    public DiaryCreateRequest(
            @JsonProperty(value = "issued_date", required = true) LocalDate issuedDate,
            @JsonProperty(value = "emotion", required = true) String emotion,
            @JsonProperty(value = "content", required = true) String content,
            @JsonProperty(value = "is_opened", required = true) Boolean isOpened
    ) {
        this.issuedDate = issuedDate;
        this.emotion = emotion;
        this.content = content;
        this.isOpened = isOpened;
    }
}
