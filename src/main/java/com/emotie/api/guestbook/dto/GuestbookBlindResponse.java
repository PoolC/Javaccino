package com.emotie.api.guestbook.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class GuestbookBlindResponse {
    private final Boolean isBlinded;

    @JsonCreator
    public GuestbookBlindResponse(
            @JsonProperty("isBlinded") Boolean isBlinded
    ) {
        this.isBlinded = isBlinded;
    }
}
