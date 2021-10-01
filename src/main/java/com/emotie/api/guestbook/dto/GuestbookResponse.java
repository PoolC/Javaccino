package com.emotie.api.guestbook.dto;

import com.emotie.api.guestbook.domain.Guestbook;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class GuestbookResponse {
    private Long id;
    private String writerId;
    private String content;
    private Integer reportCount;
    private LocalDateTime createdAt;

    public static GuestbookResponse of(Guestbook guestbook) {
        return GuestbookResponse.builder()
                .id(guestbook.getId())
                .writerId(guestbook.getWriter().getUUID())
                .content(guestbook.getContent())
                .reportCount(guestbook.getReportCount())
                .createdAt(guestbook.getCreatedAt())
                .build();
    }

    @JsonCreator
    @Builder
    public GuestbookResponse(@JsonProperty("id") Long id, @JsonProperty("writerId") String writerId,
                             @JsonProperty("content") String content, @JsonProperty("reportCount") Integer reportCount,
                             @JsonProperty("createdAt") LocalDateTime createdAt) {
        this.id = id;
        this.writerId = writerId;
        this.content = content;
        this.reportCount = reportCount;
        this.createdAt = createdAt;
    }
}
