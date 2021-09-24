package com.emotie.api.diary.dto;

import com.emotie.api.diary.domain.Diary;
import lombok.Getter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Getter
public class DiaryReadResponse {
    private final Integer id;
    private final String author;
    private final String emotion;
    private final String issuedDate;
    private final String content;
    private final Boolean isOpened;

    public DiaryReadResponse(
            Diary diary
    ) {
        this.id = diary.getId();
        this.author = diary.getWriter().getNickname();
        this.emotion = diary.getEmotion().getEmotion();
        this.issuedDate = diary.getIssuedDate().format(DateTimeFormatter.ISO_DATE);
        this.content = diary.getContent();
        this.isOpened = diary.getIsOpened();
    }

}
