package com.emotie.api.emotion.domain;

import com.emotie.api.member.domain.Member;
import lombok.Getter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@Getter
@DiscriminatorValue("JEALOUS")
public class JealousEmotion extends Emotion {
    private static final String COLOR = "#9431A4";
    private static final String DESCRIPTION = "jealous";
    private static final String NAME = "질투";

    protected JealousEmotion() {
    }

    public JealousEmotion(Member member) {
        this(member, 0.0);
    }

    public JealousEmotion(Member member, double score) {
        super(member, score, COLOR, DESCRIPTION, NAME);
    }
}