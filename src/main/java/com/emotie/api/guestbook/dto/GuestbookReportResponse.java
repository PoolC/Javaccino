package com.emotie.api.guestbook.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class GuestbookReportResponse {
    private final Boolean isReported;

    @JsonCreator
    public GuestbookReportResponse(
            @JsonProperty("isReported") Boolean isReported
    ) {
        this.isReported = isReported;
    }
}
