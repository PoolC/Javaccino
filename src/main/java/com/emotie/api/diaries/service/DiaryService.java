package com.emotie.api.diaries.service;

import com.emotie.api.auth.exception.UnauthorizedException;
import com.emotie.api.common.domain.Postings;
import com.emotie.api.diaries.domain.Diary;
import com.emotie.api.diaries.domain.Emotion;
import com.emotie.api.diaries.dto.DiaryCreateRequest;
import com.emotie.api.diaries.dto.DiaryDeleteRequest;
import com.emotie.api.diaries.dto.DiaryUpdateRequest;
import com.emotie.api.diaries.exception.DuplicatedArgumentsException;
import com.emotie.api.diaries.repository.DiaryRepository;
import com.emotie.api.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

@SuppressWarnings("unused")
@Service
@RequiredArgsConstructor
public class DiaryService {
    private final DiaryRepository diaryRepository;

    public void create(Member user, DiaryCreateRequest request) {
        checkCreateRequestValidity(request);
        // TODO: 2021-09-16 Writer user가 현재 EmotionStatus 때문에 Serializable 하지 않음; 
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
        checkDeleteRequestValidity(user, request);
        List<Integer> id = request.getId();
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

    private void checkDeleteRequestValidity(Member user, DiaryDeleteRequest request) {
        checkDeleteListValidity(user, request.getId());
    }

    private void checkIfContentIsValid(String content) {

    }

    private void checkIfUserValid(Member user, Diary diary) {
        if (!user.equals(diary.getWriter())) throw new UnauthorizedException("작성자만이 수정할 수 있습니다.");
    }

    private void checkDeleteListValidity(Member user, List<Integer> id) {
        ArrayList<Integer> usedId = new ArrayList<>();
        id.forEach(
                (diaryId) -> {
                    if (usedId.contains(diaryId)) throw new DuplicatedArgumentsException("요청에 중복된 ID가 존재합니다.");
                    usedId.add(diaryId);
                    Diary diary = getDiaryById(diaryId);
                    if (!user.equals(diary.getWriter())) throw new UnauthorizedException("삭제를 요청한 사람에게 해당 권한이 없습니다.");
                    if (diary.getReportCount() >= Postings.reportCountThreshold)
                        throw new UnauthorizedException("삭제를 요청한 대상이 신고가 누적되어 삭제가 불가능합니다. ");
                }
        );
    }
}
