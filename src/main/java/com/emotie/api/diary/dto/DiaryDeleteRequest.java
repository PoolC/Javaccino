package com.emotie.api.diary.dto;

import com.emotie.api.diary.domain.DiaryIds;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Set;

@Getter
public class DiaryDeleteRequest {

    private final Set<Long> diaryId;

    @Builder
    @JsonCreator
    public DiaryDeleteRequest(
            @JsonProperty(value = "diaryId", required = true) Set<Long> diaryId
    ) {
        this.diaryId = diaryId;
    }
}
