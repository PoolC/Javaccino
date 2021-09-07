package com.emotie.api.diaries.vo;

import lombok.Getter;

@Getter
public class EmotionStatus {
    private final double score;
    private final int count;

    public EmotionStatus(double score, int count) {
        this.score = score;
        this.count = count;
    }
}
