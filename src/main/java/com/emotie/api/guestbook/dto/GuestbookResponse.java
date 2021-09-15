package com.emotie.api.guestbook.dto;

import com.emotie.api.guestbook.domain.Guestbook;
import com.emotie.api.member.domain.Member;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class GuestbookResponse {
    private final Integer id;
    private final Member owner;
    private final Member writer;
    private final String content;
    private final Integer reportCount;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    @JsonCreator
    public GuestbookResponse(Guestbook guestbook) {
        this.id = guestbook.getId();
        this.owner = guestbook.getOwner();
        this.writer = guestbook.getWriter();
        this.content = guestbook.getContent();
        this.reportCount = guestbook.getReportCount();
        this.createdAt = guestbook.getCreatedAt();
        this.updatedAt = guestbook.getUpdateAt();
    }
}
