package com.emotie.api.diaries.service;

import com.emotie.api.diaries.domain.Diary;
import com.emotie.api.diaries.dto.DiaryCreateRequest;
import com.emotie.api.diaries.repository.DiaryRepository;
import com.emotie.api.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

// TODO: 2021-08-06
@SuppressWarnings("unused")
@Service
@RequiredArgsConstructor
public class DiaryService {
    private final DiaryRepository diariesRepository;

    public void create(Member user, DiaryCreateRequest diaryCreateRequest) {
        diariesRepository.save(
                Diary.builder()
                        .writerId(user.getUUID())
                        .emotion(diaryCreateRequest.getEmotion())
                        .content(diaryCreateRequest.getContent())
                        .isOpened(diaryCreateRequest.getIsOpened())
                        .build()
        );
    }
}
