package com.emotie.api.diary.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

@Getter
@Embeddable
@NoArgsConstructor
public class EmotionStatus {
    private static final double TRANSFER_WEIGHT = 0.79;
    private static final double REVERSE_TRANSFER_WEIGHT = 1/TRANSFER_WEIGHT;
    private double score;
    private int count;

    public EmotionStatus(Double score, Integer count) {
        this.score = score;
        this.count = count;
    }

    public void addOne() {
        this.count += 1;
    }

    public void removeOne() {
        if (this.count <= 0) throw new IndexOutOfBoundsException("글 개수가 음수가 될 수는 없습니다.");
        this.count -= 1;
    }

    public void deepenScore(Double scoreUpdater) {
        this.score = TRANSFER_WEIGHT * this.score + (1 - TRANSFER_WEIGHT) * scoreUpdater;
    }

    public void reduceScore(Double scoreUpdater) {
        this.score = REVERSE_TRANSFER_WEIGHT * this.score + (1 - REVERSE_TRANSFER_WEIGHT) * scoreUpdater;
    }
}
