package com.emotie.api.member.domain;

import com.emotie.api.emotion.domain.Emotion;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity(name = "emotion_scores")
public class EmotionScore {
    private static final Double TRANSFER_WEIGHT = 0.79;
    private static final Double REVERSE_TRANSFER_WEIGHT = 1/TRANSFER_WEIGHT;

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;

    @Column(name = "member_id")
    private String memberId;

    @ManyToOne(targetEntity = Emotion.class, fetch = FetchType.LAZY, cascade = {CascadeType.ALL})
    @JoinColumn(name = "emotion_id")
    private Emotion emotion;

    @Column(name = "score")
    private Double score;

    @Column(name = "count")
    private Integer count;

    private EmotionScore(String memberId, Emotion emotion, Double score) {
        this.memberId = memberId;
        this.emotion = emotion;
        this.score = score;
        this.count = 0;
    }

    public static EmotionScore of(String memberId, Emotion emotion, Double score) {
        return new EmotionScore(memberId, emotion, score);
    }

    public void deepenScore(Double amount) {
        this.score = TRANSFER_WEIGHT * this.score + (1 - TRANSFER_WEIGHT) * amount;
    }

    public void reduceScore(Double amount) {
        this.score = REVERSE_TRANSFER_WEIGHT * this.score + (1 - REVERSE_TRANSFER_WEIGHT) * amount;
    }

    public void addOne() {
        this.count++;
    }

    public void removeOne() {
        if (this.count <= 0) throw new IndexOutOfBoundsException("글의 개수가 음수가 될 수는 없습니다.");
        this.count--;
    }
}
