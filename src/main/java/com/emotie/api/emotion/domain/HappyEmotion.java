package com.emotie.api.emotion.domain;

import com.emotie.api.member.domain.Member;
import lombok.Getter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@Getter
@DiscriminatorValue("HAPPY")
public class HappyEmotion extends Emotion {
    private static final String COLOR = "#FFF27D";
    private static final String DESCRIPTION = "기쁨";
    private static final String NAME = "happy";

    protected HappyEmotion() {
    }

    public HappyEmotion(Member member) {
        this(member, 0.0);
    }

    public HappyEmotion(Member member, double score) {
        super(member, score, COLOR, DESCRIPTION, NAME);
    }
}