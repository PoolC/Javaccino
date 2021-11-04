package com.emotie.api.diary.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

import java.util.List;

@Getter
public class DiaryReadAllResponse {
    private final List<DiaryReadResponse> diaries;

    @JsonCreator
    public DiaryReadAllResponse(
            List<DiaryReadResponse> diaries
    ) {
        this.diaries = diaries;
    }
}
