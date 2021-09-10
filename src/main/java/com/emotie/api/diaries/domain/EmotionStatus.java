package com.emotie.api.diaries.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

@Getter
@Embeddable
@NoArgsConstructor
public class EmotionStatus {
    private static final double TRANSFER_WEIGHT = 0.79;
    private double score;
    private int count;

    public EmotionStatus(double score, int count) {
        this.score = score;
        this.count = count;
    }

    public void addOne() {
        this.count += 1;
    }

    public void updateScore(double newScore) {
        this.score = TRANSFER_WEIGHT * this.score + (1 - TRANSFER_WEIGHT) * newScore;
    }
}
