package com.emotie.api.guestbook.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Deprecated
@Getter
public class GuestbookLocalBlindResponse {
    private final Boolean isLocalBlinded;

    @JsonCreator
    public GuestbookLocalBlindResponse(
            @JsonProperty("isLocalBlinded") Boolean isLocalBlinded
    ) {
        this.isLocalBlinded = isLocalBlinded;
    }
}
