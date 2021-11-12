package com.emotie.api.emotion.domain;

import com.emotie.api.member.domain.Member;
import lombok.Getter;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Getter
@DiscriminatorColumn(name = "DISCRIMINATOR", discriminatorType = DiscriminatorType.STRING)
@Table(name = "emotions")
public abstract class Emotion {
    private static final Double TRANSFER_WEIGHT = 0.79;
    private static final Double REVERSE_TRANSFER_WEIGHT = 1/TRANSFER_WEIGHT;

    @Id
    @GeneratedValue
    private Long Id;

    @ManyToOne(targetEntity = Member.class, fetch = FetchType.EAGER)
    @JoinColumn(name ="member_id")
    protected Member member;

    @Column(name = "score")
    protected Double score;

    @Column(name = "name")
    protected String name;

    @Column(name = "color")
    protected String color;

    @Column(name = "description")
    protected String description;

    protected Emotion() {
    }

    protected Emotion(Member member, double score, String color, String description, String name) {
        this.member = member;
        this.score = score;
        this.color = color;
        this.description = description;
        this.name = name;
    }

    public void deepenScore(Integer amount) {
        this.score = TRANSFER_WEIGHT * this.score + (1 - TRANSFER_WEIGHT) * amount;
        if (this.score > 1) this.score = 1.0;
    }

    public void reduceScore(Integer amount) {
        this.score = REVERSE_TRANSFER_WEIGHT * this.score + (1 - REVERSE_TRANSFER_WEIGHT) * amount;
        if (this.score < 0) this.score = 0.0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Emotion emotion = (Emotion) o;
        return member.equals(emotion.member) && name.equals(emotion.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(member, name);
    }
}
