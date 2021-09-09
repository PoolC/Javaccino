package com.emotie.api.diaries.domain;

import com.emotie.api.common.domain.Postings;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.util.*;

@Getter
@NoArgsConstructor
@Entity(name = "emodiaries")
public class Diaries extends Postings {
    @Column(name = "emotion", nullable = false)
    private Emotion emotion;

    @Column(name = "is_opened", nullable = false)
    private Boolean isOpened;

    @Builder
    public Diaries(
            Integer writerId, String content, Emotion emotion, Boolean isOpened
    ) {
        this.writerId = writerId;
        this.content = content;
        this.emotion = emotion;
        this.isOpened = isOpened;
        this.reportCount = 0;
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
