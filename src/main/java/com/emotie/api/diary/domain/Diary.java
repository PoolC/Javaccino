package com.emotie.api.diary.domain;

import com.emotie.api.common.domain.Postings;
import com.emotie.api.member.domain.Member;
import com.emotie.api.emotion.domain.Emotion;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@NoArgsConstructor
@Entity(name = "emodiaries")
public class Diary extends Postings {
    @Column(name = "issued_date", nullable = false)
    private LocalDate issuedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="emotion_tag_id")
    private Emotion emotion;

    @Column(name = "is_opened", nullable = false)
    private Boolean isOpened;

    @Builder
    private Diary(
            LocalDate issuedDate, Member writer, String content, Emotion emotion, Boolean isOpened
    ) {
        this.issuedDate = issuedDate;
        this.writer = writer;
        this.content = content;
        this.emotion = emotion;
        this.isOpened = isOpened;
        this.reportCount = 0;


    }

    public static Diary of(
            LocalDate issuedDate, Member writer, String content, Emotion emotion, Boolean isOpened
    ) {
        return new Diary(issuedDate, writer, content, emotion, isOpened);
    }

    public void updateIssuedDate(LocalDate updatingDate) { this.issuedDate = updatingDate; }

    public void updateEmotion(Emotion updatingEmotion) { this.emotion = updatingEmotion; }

    public void updateOpenness(Boolean updatingOpenness) { this.isOpened = updatingOpenness; }

    @Override
    public Postings readPosting() {
        return this;
    }

    @Override
    public Postings reportPosting() {
        return this;
    }
}
