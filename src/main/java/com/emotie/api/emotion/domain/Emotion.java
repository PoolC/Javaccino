package com.emotie.api.emotion.domain;


import com.emotie.api.common.domain.TimestampEntity;
import com.emotie.api.diaries.domain.Diaries;
import groovy.lang.Lazy;
import lombok.Getter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity(name="emotions")
@Getter
public class Emotion extends TimestampEntity {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private Integer id;

    @Column(name = "emotion")
    private String name;

    @Column(name="color")
    private String color;

    @Column(name="priority")
    private Integer priority;

    @OneToMany(mappedBy = "emotion")
    private List<Diaries> diariesList = new ArrayList<>();

    protected Emotion(){
    }

    private Emotion(String name, String color, Integer priority){
        this.name = name;
        this.color = color;
        this.priority = priority;
    }

    public static Emotion of(String name, String color, Integer priority){
        return new Emotion(name, color, priority);
    }




}
