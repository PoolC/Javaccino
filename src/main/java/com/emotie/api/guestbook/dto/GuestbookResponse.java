package com.emotie.api.guestbook.dto;

import com.emotie.api.guestbook.domain.Guestbook;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class GuestbookResponse {
    private Long guestbookId;
    private String nickname;
    private String content;
    private LocalDateTime date;

    public static GuestbookResponse of(Guestbook guestbook) {
        return GuestbookResponse.builder()
                .guestbookId(guestbook.getId())
                .nickname(guestbook.getWriter().getNickname())
                .content(guestbook.getContent())
                .date(guestbook.getCreatedAt())
                .build();
    }

    @JsonCreator
    @Builder
    public GuestbookResponse(@JsonProperty("guestbookId") Long guestbookId, @JsonProperty("nickname") String nickname,
                             @JsonProperty("content") String content,
                             @JsonProperty("date") LocalDateTime date) {
        this.guestbookId = guestbookId;
        this.nickname = nickname;
        this.content = content;
        this.date = date;
    }
}
