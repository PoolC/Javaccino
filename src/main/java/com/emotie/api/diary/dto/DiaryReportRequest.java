package com.emotie.api.diary.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
public class DiaryReportRequest {

    // TODO: 내용 글자수 제한 정하고 반영할 것
    @NotBlank(message = "신고 사유를 입력해주세요.")
    @Size(message = "내용은 50글자보다 짧고, 적어도 1글자 이상이어야 합니다.", max = 50)
    private final String reason;

    @JsonCreator
    @Builder
    public DiaryReportRequest(
            @JsonProperty(value = "reason", required = true) String reason
    ) {
        this.reason = reason;
    }
}