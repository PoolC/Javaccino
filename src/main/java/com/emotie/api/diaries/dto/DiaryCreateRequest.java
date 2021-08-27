package com.emotie.api.diaries.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;

@Getter
public class DiaryCreateRequest {
    @NotNull
    @PastOrPresent(message = "죄송합니다. 시간 여행을 기록하실 수는 없습니다.")
    private final LocalDate issuedDate;

    @NotNull
    @Min(message = "감정 태그는 음이 아닌 정수여야 합니다.", value = 0)
    private final Integer emotionTagId;

    @NotNull
    private final String content;

    @NotNull
    private final Boolean isOpened;

    @Builder
    @JsonCreator
    public DiaryCreateRequest(
            @JsonProperty(value = "issued_date") LocalDate issuedDate,
            @JsonProperty(value = "emotion_tag_id") Integer emotionTagId,
            @JsonProperty(value = "content") String content,
            @JsonProperty(value = "is_opened") Boolean isOpened
    ) {
        this.issuedDate = issuedDate;
        this.emotionTagId = emotionTagId;
        this.content = content;
        this.isOpened = isOpened;
    }
}
