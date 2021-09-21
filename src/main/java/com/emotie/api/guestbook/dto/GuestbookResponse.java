package com.emotie.api.guestbook.dto;

import com.emotie.api.guestbook.domain.Guestbook;
import com.emotie.api.member.domain.Member;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
public class GuestbookResponse {
    private Integer id;
    private String ownerId;
    private String writerId;
    private String content;
    private Integer reportCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static GuestbookResponse of(Guestbook guestbook) {
        return GuestbookResponse.builder()
                .id(guestbook.getId())
                .ownerId(guestbook.getOwner().getUUID())
                .writerId(guestbook.getWriter().getUUID())
                .content(guestbook.getContent())
                .reportCount(guestbook.getReportCount())
                .createdAt(guestbook.getCreatedAt())
                .updatedAt(guestbook.getUpdateAt())
                .build();
    }

    @JsonCreator
    @Builder
    public GuestbookResponse(@JsonProperty("id") Integer id, @JsonProperty("ownerId") String ownerId, @JsonProperty("writerId") String writerId,
                             @JsonProperty("content") String content, @JsonProperty("reportCount") Integer reportCount,
                             @JsonProperty("createdAt") LocalDateTime createdAt, @JsonProperty("updatedAt") LocalDateTime updatedAt) {
        this.id = id;
        this.ownerId = ownerId;
        this.writerId = writerId;
        this.content = content;
        this.reportCount = reportCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
