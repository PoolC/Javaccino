package com.emotie.api.member.domain;

import com.emotie.api.emotion.domain.Emotion;
import lombok.Getter;

import javax.persistence.*;

@Getter
@Entity(name = "emotion_scores")
public class EmotionScores {
    private static final Double TRANSFER_WEIGHT = 0.79;
    private static final Double REVERSE_TRANSFER_WEIGHT = 1/TRANSFER_WEIGHT;

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @Column(name = "member_id")
    private Long memberId;

    @ManyToOne(targetEntity = Emotion.class, fetch = FetchType.LAZY, cascade = {CascadeType.ALL})
    @JoinColumn(name = "emotion_id")
    private Emotion emotion;

    @Column(name = "score")
    private Double score;

    @Column(name = "count")
    private Long count;

    public void deepen(Double amount) {
        addOne();
        deepenScore(amount);
    }

    public void reduce(Double amount) {
        removeOne();
        reduceScore(amount);
    }

    private void deepenScore(Double amount) {
        this.score = TRANSFER_WEIGHT * this.score + (1 - TRANSFER_WEIGHT) * amount;
    }

    private void reduceScore(Double amount) {
        this.score = REVERSE_TRANSFER_WEIGHT * this.score + (1 - REVERSE_TRANSFER_WEIGHT) * amount;
    }

    private void addOne() {
        this.count++;
    }

    private void removeOne() {
        if (this.count <= 0) throw new IndexOutOfBoundsException("글의 개수가 음수가 될 수는 없습니다.");
        this.count--;
    }
}
