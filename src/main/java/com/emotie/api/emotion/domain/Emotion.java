package com.emotie.api.emotion.domain;


import com.emotie.api.common.domain.TimestampEntity;
import com.emotie.api.diary.domain.Diary;
import lombok.Builder;
import lombok.Getter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "emotions")
@Getter
public class Emotion extends TimestampEntity {

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

    @OneToMany(mappedBy = "emotion")
    private List<Diary> diariesList = new ArrayList<>();

    protected Emotion() {
    }

    @Builder
    private Emotion(String emotion, String color) {
        this.emotion = emotion;
        this.color = color;
        this.priority = priority;
    }

    public static Emotion of(String emotion, String color) {
        return new Emotion(emotion, color);
    }

    public void update(String emotion, String color) {
        this.emotion = emotion;
        this.color = color;
    }


}
