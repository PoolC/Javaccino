package com.emotie.api.diary.service;

import com.emotie.api.auth.exception.UnauthorizedException;
import com.emotie.api.common.domain.Postings;
import com.emotie.api.diary.domain.Diary;
import com.emotie.api.emotion.domain.Emotion;
import com.emotie.api.diary.dto.DiaryCreateRequest;
import com.emotie.api.diary.dto.DiaryDeleteRequest;
import com.emotie.api.diary.dto.DiaryUpdateRequest;
import com.emotie.api.diary.repository.DiaryRepository;
import com.emotie.api.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

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
                        .writer(user)
                        .emotion(request.getEmotion())
                        .content(request.getContent())
                        .isOpened(request.getIsOpened())
                        .build()
        );
    }

    public Emotion update(Member user, Integer diaryId, DiaryUpdateRequest request) {
        Diary diary = getDiaryById(diaryId);
        Emotion originalEmotion = diary.getEmotion();

        checkUpdateRequestValidity(user, request, diary);

        diary.setIssuedDate(request.getIssuedDate());
        diary.setEmotion(request.getEmotion());
        diary.setContent(request.getContent());
        diary.setIsOpened(request.getIsOpened());
        diaryRepository.saveAndFlush(diary);

        return originalEmotion;
    }

    public List<Emotion> delete(Member user, DiaryDeleteRequest request) {
        Set<Integer> id = new HashSet<>(request.getId());
        checkDeleteRequestValidity(user, id);
        LinkedList<Emotion> emotions = new LinkedList<>();
        id.stream().map(this::getDiaryById).forEach(
                (diary) -> {
                    emotions.add(diary.getEmotion());
                    diaryRepository.delete(diary);
                }
        );

        return emotions;
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

    private void checkDeleteRequestValidity(Member user, Set<Integer> id) {
        checkDeleteListValidity(user, id);
    }

    private void checkIfContentIsValid(String content) {

    }

    private void checkIfUserValid(Member user, Diary diary) {
        if (!user.equals(diary.getWriter())) throw new UnauthorizedException("작성자만이 수정할 수 있습니다.");
    }

    private void checkDeleteListValidity(Member user, Set<Integer> id) {
        id.forEach(
                (diaryId) -> {
                    Diary diary = getDiaryById(diaryId);
                    if (!user.equals(diary.getWriter())) throw new UnauthorizedException("삭제를 요청한 사람에게 해당 권한이 없습니다.");
                    if (diary.getReportCount() >= Postings.reportCountThreshold)
                        throw new UnauthorizedException("삭제를 요청한 대상이 신고가 누적되어 삭제가 불가능합니다.");
                }
        );
    }
}
