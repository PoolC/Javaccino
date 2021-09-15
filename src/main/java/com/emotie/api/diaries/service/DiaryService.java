package com.emotie.api.diaries.service;

import com.emotie.api.diaries.domain.Diary;
import com.emotie.api.diaries.dto.DiaryCreateRequest;
import com.emotie.api.diaries.dto.DiaryUpdateRequest;
import com.emotie.api.diaries.repository.DiaryRepository;
import com.emotie.api.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@SuppressWarnings("unused")
@Service
@RequiredArgsConstructor
public class DiaryService {
    private final DiaryRepository diaryRepository;

    public void create(Member user, DiaryCreateRequest diaryCreateRequest) {
        checkIfContentIsValid(diaryCreateRequest.getContent());
        diaryRepository.save(
                Diary.builder()
                        .issuedDate(diaryCreateRequest.getIssuedDate())
                        .writerId(user.getUUID())
                        .emotion(diaryCreateRequest.getEmotion())
                        .content(diaryCreateRequest.getContent())
                        .isOpened(diaryCreateRequest.getIsOpened())
                        .build()
        );
    }

    public void update(Member user, Integer diaryId, DiaryUpdateRequest diaryUpdateRequest) {
        checkIfContentIsValid(diaryUpdateRequest.getContent());
        Diary diary = getDiaryById(diaryId);
        diary.setIssuedDate(diaryUpdateRequest.getIssuedDate());
        diary.setEmotion(diaryUpdateRequest.getEmotion());
        diary.setContent(diaryUpdateRequest.getContent());
        diary.setIsOpened(diaryUpdateRequest.getIsOpened());
        diaryRepository.saveAndFlush(diary);
    }

    private Diary getDiaryById(Integer diaryId) {
        return diaryRepository.findById(diaryId).orElseThrow(
                () -> new NoSuchElementException("해당하는 아이디의 다이어리가 없습니다.")
        );
    }

    private void checkIfContentIsValid(String content) {

    }
}
