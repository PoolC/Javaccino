package com.emotie.api.diaries.domain;

import com.emotie.api.common.domain.Postings;
import com.emotie.api.member.domain.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor
@Entity(name = "emodiaries")
public class Diaries extends Postings {
    @Column(name = "emotion_tag_id", nullable = false)
    private Integer emotionTagId;

    @Column(name = "is_opened", nullable = false)
    private Boolean isOpened;


    @Builder
    public Diaries(
            Member writer, String content, Integer emotionTagId, Boolean isOpened, Integer reportCount
    ) {
        this.writer = writer;
        this.content = content;
        this.emotionTagId = emotionTagId;
        this.isOpened = isOpened;
        this.reportCount = reportCount;
    }

    @Override
    public Postings readPosting() {
        return this;
    }

    @Override
    public Postings reportPosting() {
        return this;
    }
}
