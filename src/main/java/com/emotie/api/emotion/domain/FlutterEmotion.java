package com.emotie.api.emotion.domain;

import com.emotie.api.member.domain.Member;
import lombok.Getter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@Getter
@DiscriminatorValue("SEOLEM")
public class FlutterEmotion extends Emotion {
    private static final String COLOR = "#F29CB6";
    private static final String DESCRIPTION = "flutter";
    private static final String NAME = "설렘";

    protected FlutterEmotion() {
    }

    public FlutterEmotion(Member member) {
        this(member, 0.0);
    }

    public FlutterEmotion(Member member, double score) {
        super(member, score, COLOR, DESCRIPTION, NAME);
    }
}