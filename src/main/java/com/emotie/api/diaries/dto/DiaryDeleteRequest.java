package com.emotie.api.diaries.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Builder;
import lombok.Getter;

@Getter
public class DiaryDeleteRequest {



    @Builder
    @JsonCreator
    public DiaryDeleteRequest() {

    }
}
