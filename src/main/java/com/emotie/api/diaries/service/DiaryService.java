package com.emotie.api.diaries.service;

import com.emotie.api.auth.exception.UnauthorizedException;
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

    public void create(Member user, DiaryCreateRequest request) {
        checkCreateRequestValidity(request);
        diaryRepository.save(
                Diary.builder()
                        .issuedDate(request.getIssuedDate())
                        .writerId(user.getUUID())
                        .emotion(request.getEmotion())
                        .content(request.getContent())
                        .isOpened(request.getIsOpened())
                        .build()
        );
    }

    public void update(Member user, Integer diaryId, DiaryUpdateRequest request) {
        Diary diary = getDiaryById(diaryId);

        checkUpdateRequestValidity(user, request, diary);

        diary.setIssuedDate(request.getIssuedDate());
        diary.setEmotion(request.getEmotion());
        diary.setContent(request.getContent());
        diary.setIsOpened(request.getIsOpened());
        diaryRepository.saveAndFlush(diary);
    }

    private Diary getDiaryById(Integer diaryId) {
        return diaryRepository.findById(diaryId).orElseThrow(
                () -> new NoSuchElementException("해당하는 아이디의 다이어리가 없습니다.")
        );
    }

    private void checkCreateRequestValidity(DiaryCreateRequest request) {
        checkIfContentIsValid(request.getContent());
    }

    private void checkUpdateRequestValidity(Member user, DiaryUpdateRequest request, Diary diary) {
        checkIfContentIsValid(request.getContent());
        checkIfUserValid(user, diary);
    }

    private void checkIfContentIsValid(String content) {

    }

    private void checkIfUserValid(Member user, Diary diary) {
        if (!user.getUUID().equals(diary.getWriterId())) throw new UnauthorizedException("작성자만이 수정할 수 있습니다.");
    }
}
