package com.emotie.api.diary.service;

import com.emotie.api.auth.exception.UnauthorizedException;
import com.emotie.api.common.domain.Postings;
import com.emotie.api.diary.domain.Diary;
import com.emotie.api.diary.dto.DiaryCreateRequest;
import com.emotie.api.diary.dto.DiaryDeleteRequest;
import com.emotie.api.diary.dto.DiaryReadResponse;
import com.emotie.api.diary.dto.DiaryUpdateRequest;
import com.emotie.api.diary.repository.DiaryRepository;
import com.emotie.api.emotion.domain.Emotion;
import com.emotie.api.emotion.repository.EmotionRepository;
import com.emotie.api.emotion.service.EmotionService;
import com.emotie.api.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@SuppressWarnings("unused")
@Service
@RequiredArgsConstructor
public class DiaryService {
    private final DiaryRepository diaryRepository;
    private final EmotionRepository emotionRepository;
    private final EmotionService emotionService;

    @Transactional
    public void create(Member member, DiaryCreateRequest request) {
        emotionService.deepenEmotionScore(member, request.getEmotion());

        diaryRepository.save(
                Diary.builder()
                        .writer(member)
                        .emotion(emotionService.getEmotionByMemberAndEmotionName(member, request.getEmotion()))
                        .content(request.getContent())
                        .isOpened(request.getIsOpened())
                        .build()
        );
    }

    public DiaryReadResponse read(Member user, Long diaryId) {
        Diary diary = getDiaryById(diaryId);
        return new DiaryReadResponse(diary.read(user));
    }

    @Deprecated
    public String update(Member user, Long diaryId, DiaryUpdateRequest request) {
        Diary diary = getDiaryById(diaryId);
        Emotion originalEmotion = diary.getEmotion();
//
//        diary.checkUserValidity(user);
//
//        Emotion updatingEmotion = getEmotionByEmotion(request.getEmotion());
//
//        updateDiaryWithRequest(diary, request);
//        diaryRepository.saveAndFlush(diary);
//        emotionRepository.saveAndFlush(originalEmotion);
//        emotionRepository.saveAndFlush(updatingEmotion);

        return originalEmotion.getName();
    }

    public List<String> delete(Member user, DiaryDeleteRequest request) {
        Set<Long> id = new HashSet<>(request.getDiaryId());
        checkDeleteListValidity(user, id);
        LinkedList<String> emotions = new LinkedList<>();
//        id.stream().map(this::getDiaryById).forEach(
//                (diary) -> {
//                    emotions.add(diary.getEmotion().getEmotion());
//                    diaryRepository.delete(diary);
//                }
//        );

        return emotions;
    }

    private Diary getDiaryById(Long diaryId) {
        return diaryRepository.findById(diaryId).orElseThrow(
                () -> new NoSuchElementException("해당하는 아이디의 다이어리가 없습니다.")
        );
    }

    private void checkDeleteListValidity(Member user, Set<Long> id) {
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
