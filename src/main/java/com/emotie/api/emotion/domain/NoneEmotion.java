package com.emotie.api.emotion.domain;

import com.emotie.api.member.domain.Member;
import lombok.Getter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@Getter
@DiscriminatorValue("NEUTRAL")
public class NeutralEmotion extends Emotion {
    private static final String COLOR = "#FFFFFF";
    private static final String DESCRIPTION = "무감정";
    private static final String NAME = "neutral";

    protected NeutralEmotion() {
    }

    public NeutralEmotion(Member member) {
        this(member, 0.0);
    }

    public NeutralEmotion(Member member, double score) {
        super(member, score, COLOR, DESCRIPTION, NAME);
    }
}