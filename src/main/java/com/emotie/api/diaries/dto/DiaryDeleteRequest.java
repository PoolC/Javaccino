package com.emotie.api.diaries.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class DiaryDeleteRequest {

    private final List<Integer> id;

    @Builder
    @JsonCreator
    public DiaryDeleteRequest(
            @JsonProperty(value = "id", required = true) List<Integer> id
    ) {
        this.id = id;
    }
}
