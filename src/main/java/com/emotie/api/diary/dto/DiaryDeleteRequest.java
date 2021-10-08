package com.emotie.api.diary.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class DiaryDeleteRequest {

    private final List<Long> diaryId;

    @Builder
    @JsonCreator
    public DiaryDeleteRequest(
            @JsonProperty(value = "diaryId", required = true) List<Long> diaryId
    ) {
        this.diaryId = diaryId;
    }
}
