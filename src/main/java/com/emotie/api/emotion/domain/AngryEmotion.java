package com.emotie.api.emotion.domain;

import com.emotie.api.member.domain.Member;
import lombok.Getter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@Getter
@DiscriminatorValue("ANGRY")
public class AngryEmotion extends Emotion {
    private static final String COLOR = "#FF855E";
    private static final String DESCRIPTION = "angry";
    private static final String NAME = "화남";

    protected AngryEmotion() {
    }

    public AngryEmotion(Member member) {
        this(member, 0.0);
    }

    public AngryEmotion(Member member, double score) {
        super(member, score, COLOR, DESCRIPTION, NAME);
    }
}