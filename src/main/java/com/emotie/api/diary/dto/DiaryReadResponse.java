package com.emotie.api.diary.dto;

import com.emotie.api.diary.domain.Diary;
import com.emotie.api.emotion.domain.Emotion;
import com.emotie.api.emotion.dto.EmotionResponse;
import lombok.Getter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Getter
public class DiaryReadResponse {
    private final Integer diaryId;
    private final String nickname;
    private final EmotionResponse emotion;
    private final String date;
    private final String content;
    private final Boolean isOpened;

    public DiaryReadResponse(
            Diary diary
    ) {
        this.diaryId = diary.getId();
        this.nickname = diary.getWriter().getNickname();
        this.emotion = new EmotionResponse(diary.getEmotion());
        this.date = diary.getCreatedAt().format(DateTimeFormatter.ISO_DATE_TIME);
        this.content = diary.getContent();
        this.isOpened = diary.getIsOpened();
    }
}
