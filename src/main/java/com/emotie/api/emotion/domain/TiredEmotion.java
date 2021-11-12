package com.emotie.api.emotion.domain;

import com.emotie.api.member.domain.Member;
import lombok.Getter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@Getter
@DiscriminatorValue("TIRED")
public class TiredEmotion extends Emotion {
    private static final String COLOR = "#ADADAD";
    private static final String DESCRIPTION = "지침";
    private static final String NAME = "tired";

    protected TiredEmotion() {
    }

    public TiredEmotion(Member member) {
        this(member, 0.0);
    }

    public TiredEmotion(Member member, double score) {
        super(member, score, COLOR, DESCRIPTION, NAME);
    }
}