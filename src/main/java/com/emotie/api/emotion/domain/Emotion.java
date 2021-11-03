package com.emotie.api.emotion.domain;


import com.emotie.api.common.domain.TimestampEntity;
import lombok.Builder;
import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity(name = "emotions")
@Getter
public class Emotion extends TimestampEntity implements Comparable<Emotion> {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Integer id;

    @Column(name = "emotion")
    private String emotion;

    @Column(name = "color")
    private String color;

    @Column(name = "priority")
    private Integer priority;

    protected Emotion() {
    }

    @Builder
    private Emotion(String emotion, String color) {
        this.emotion = emotion;
        this.color = color;

    }

    public static Emotion of(String emotion, String color) {
        return new Emotion(emotion, color);
    }

    public void update(String emotion, String color) {
        this.emotion = emotion;
        this.color = color;
    }

    // Comparable interface
    public int compareTo(Emotion e)
    {
        // Two instance of class can be compared
        int diff = this.id - e.id;

        // Note: Two equal employee Id will return 0
        return diff;
    }


}
