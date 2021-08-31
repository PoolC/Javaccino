package com.emotie.api.guestbook.dto;

import com.emotie.api.guestbook.domain.Guestbook;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class GuestbookResponse {
    private final Integer id;
    private final String ownerId;
    private final String writerId;
    private final String content;
    private final Integer reportCount;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    @JsonCreator
    public GuestbookResponse(Guestbook guestbook) {
        this.id = guestbook.getId();
        this.ownerId = guestbook.getOwnerId();
        this.writerId = guestbook.getWriterId();
        this.content = guestbook.getContent();
        this.reportCount = guestbook.getReportCount();
        this.createdAt = guestbook.getCreatedAt();
        this.updatedAt = guestbook.getUpdateAt();
    }
}
