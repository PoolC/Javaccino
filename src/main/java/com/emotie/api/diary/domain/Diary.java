package com.emotie.api.diary.domain;

import com.emotie.api.auth.exception.UnauthorizedException;
import com.emotie.api.common.domain.Postings;
import com.emotie.api.diary.exception.PeekingPrivatePostException;
import com.emotie.api.emotion.domain.Emotion;
import com.emotie.api.member.domain.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity(name = "emodiaries")
public class Diary extends Postings {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emotion_tag_id")
    private Emotion emotion;

    @Column(name = "is_opened", nullable = false)
    private Boolean isOpened;

    @Builder
    private Diary(
            Member writer, String content, Emotion emotion, Boolean isOpened
    ) {

        this.writer = writer;
        this.content = content;
        this.emotion = emotion;
        this.isOpened = isOpened;
        this.reportCount = 0;


    }

    public static Diary of(
            Member writer, String content, Emotion emotion, Boolean isOpened
    ) {
        return new Diary(writer, content, emotion, isOpened);
    }

    public void updateEmotion(Emotion updatingEmotion) {
        this.emotion = updatingEmotion;
    }

    public void updateOpenness(Boolean updatingOpenness) {
        this.isOpened = updatingOpenness;
    }

    public Diary read(Member user) {
        checkIsOpenedOrWriter(user);
        return this;
    }

    @Override
    public Postings readPosting() {
        return this;
    }

    @Override
    public Postings reportPosting() {
        return this;
    }

    public void checkIsOpened() {
        if (!this.isOpened) throw new UnauthorizedException("비공개 게시물에 대해 수행할 수 없는 요청입니다.");
    }

    private void checkIsOpenedOrWriter(Member user) {
        if (!this.writer.equals(user) && !this.isOpened) throw new PeekingPrivatePostException("비공개 게시물입니다.");
    }

    public void checkUserValidity(Member user) {
        if (!user.equals(this.writer)) throw new UnauthorizedException("작성자만이 권한을 갖고 있습니다.");
    }
}

