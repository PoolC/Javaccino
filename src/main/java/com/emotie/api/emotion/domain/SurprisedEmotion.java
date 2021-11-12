package com.emotie.api.emotion.domain;

import com.emotie.api.member.domain.Member;
import lombok.Getter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@Getter
@DiscriminatorValue("SURPRISED")
public class SurprisedEmotion extends Emotion {
    private static final String COLOR = "#AEE477";
    private static final String DESCRIPTION = "놀람";
    private static final String NAME = "surprised";

    protected SurprisedEmotion() {
    }

    public SurprisedEmotion(Member member) {
        this(member, 0.0);
    }

    public SurprisedEmotion(Member member, double score) {
        super(member, score, COLOR, DESCRIPTION, NAME);
    }
}