package com.emotie.api.guestbook.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Deprecated
@Getter
public class GuestbookGlobalBlindResponse {
    private final Boolean isGlobalBlinded;

    @JsonCreator
    public GuestbookGlobalBlindResponse(
            @JsonProperty("isGlobalBlinded") Boolean isGlobalBlinded
    ) {
        this.isGlobalBlinded = isGlobalBlinded;
    }
}
