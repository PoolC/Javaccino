package com.emotie.api.diary.domain;

import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

@Getter
public class DiaryIds {
    private final Set<Long> diaryIds;

    public DiaryIds(Set<Long> diaryIds) {
        this.diaryIds = diaryIds;
    }
}
