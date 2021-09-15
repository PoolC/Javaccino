package com.emotie.api.diaries.domain;

import com.emotie.api.common.domain.Postings;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@Entity(name = "emodiaries")
public class Diary extends Postings {
    @Column(name = "emotion", nullable = false)
    private Emotion emotion;

    @Column(name = "is_opened", nullable = false)
    private Boolean isOpened;

    private static Integer prevId = 0;

    @Builder
    public Diary(
            String writerId, String content, Emotion emotion, Boolean isOpened
    ) {
        this.writerId = writerId;
        this.content = content;
        this.emotion = emotion;
        this.isOpened = isOpened;
        this.reportCount = 0;
        this.id = nextId();
    }

    @Override
    public Postings readPosting() {
        return this;
    }

    @Override
    public Postings reportPosting() {
        return this;
    }

    private Integer nextId() {
        return prevId++;
    }
}
