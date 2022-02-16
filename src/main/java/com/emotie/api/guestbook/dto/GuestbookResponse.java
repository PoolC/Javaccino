package com.emotie.api.guestbook.dto;

import com.emotie.api.common.service.TimeService;
import com.emotie.api.guestbook.domain.Guestbook;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.time.format.DateTimeFormatter;

@Getter
public class GuestbookResponse {
    private Long guestbookId;
    private String writerId;
    private String nickname;
    private String content;
    private String date;

    public static GuestbookResponse of(Guestbook guestbook) {
        return GuestbookResponse.builder()
                .guestbookId(guestbook.getId())
                .writerId(guestbook.getWriter().getUUID())
                .nickname(guestbook.getWriter().getNickname())
                .content(guestbook.getContent())
                .date(TimeService.calculateTime(guestbook.getCreatedAt()))
                .build();
    }

    @JsonCreator
    @Builder
    public GuestbookResponse(@JsonProperty("guestbookId") Long guestbookId, @JsonProperty("writerId") String writerId, @JsonProperty("nickname") String nickname,
                             @JsonProperty("content") String content,
                             @JsonProperty("date") String date) {
        this.guestbookId = guestbookId;
        this.writerId = writerId;
        this.nickname = nickname;
        this.content = content;
        this.date = date;
    }
}
