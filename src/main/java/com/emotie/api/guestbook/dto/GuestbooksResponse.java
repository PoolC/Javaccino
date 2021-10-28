package com.emotie.api.guestbook.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

import java.util.List;

@Getter
public class GuestbooksResponse {
    private final List<GuestbookResponse> guestbooks;

    @JsonCreator
    public GuestbooksResponse(List<GuestbookResponse> guestbooks) {
        this.guestbooks = guestbooks;
    }
}