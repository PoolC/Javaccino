package com.emotie.api.emotion.domain;

import com.emotie.api.member.domain.Member;
import lombok.Getter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@Getter
@DiscriminatorValue("SAD")
public class SadEmotion extends Emotion {
    private static final String COLOR = "#9FA7EF";
    private static final String DESCRIPTION = "슬픔";
    private static final String NAME = "sad";

    protected SadEmotion() {
    }

    public SadEmotion(Member member) {
        this(member, 0.0);
    }

    public SadEmotion(Member member, double score) {
        super(member, score, COLOR, DESCRIPTION, NAME);
    }
}