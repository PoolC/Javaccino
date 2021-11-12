package com.emotie.api.emotion.domain;

import com.emotie.api.member.domain.Member;
import lombok.Getter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@Getter
@DiscriminatorValue("SEOLEM")
public class SeolemEmotion extends Emotion {
    private static final String COLOR = "#A29CB6";
    private static final String DESCRIPTION = "설렘";
    private static final String NAME = "seolem";

    protected SeolemEmotion() {
    }

    public SeolemEmotion(Member member) {
        this(member, 0.0);
    }

    public SeolemEmotion(Member member, double score) {
        super(member, score, COLOR, DESCRIPTION, NAME);
    }
}