package com.emotie.api.diary.service;

import com.emotie.api.auth.exception.UnauthorizedException;
import com.emotie.api.common.domain.Postings;
import com.emotie.api.diary.domain.Diary;
import com.emotie.api.diary.domain.MemberBlindDiary;
import com.emotie.api.diary.domain.MemberReportDiary;
import com.emotie.api.diary.dto.*;
import com.emotie.api.diary.repository.DiaryRepository;
import com.emotie.api.diary.repository.MemberBlindDiaryRepository;
import com.emotie.api.diary.repository.MemberReportDiaryRepository;
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

    private final MemberReportDiaryRepository memberReportDiaryRepository;
    private final MemberBlindDiaryRepository memberBlindDiaryRepository;
    private final MemberRepository memberRepository;

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

    public DiaryReadAllResponse readAll(Member user, String memberId, Integer pageNumber) {
        Member writer = getMemberById(memberId);

        Pageable page = PageRequest.of(pageNumber, PAGE_SIZE, Sort.by("createdAt").descending());
        if (user.equals(writer)) {
            List<Diary> allDiaries = diaryRepository.findAllByWriter(user, writer, Diary.reportCountThreshold, page);
            return new DiaryReadAllResponse(
                    allDiaries.stream().map(DiaryReadResponse::new).collect(Collectors.toList())
            );
        }

        List<Diary> allOpenedDiaries = diaryRepository.findAllByWriterAndIsOpened(user, writer, true, Diary.reportCountThreshold, page);
        return new DiaryReadAllResponse(
                allOpenedDiaries.stream().map(DiaryReadResponse::new).collect(Collectors.toList())
        );
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

    public void report(Member user, DiaryReportRequest request, Long diaryId) {
        checkReportOrBlindRequestValidity(user, diaryId);
        Diary target = getDiaryById(diaryId);
        target.addReportCount();
        diaryRepository.saveAndFlush(target);
        memberReportDiaryRepository.save(new MemberReportDiary(user, target, request.getReason()));
    }

    public void blind(Member user, Long diaryId) {
        checkReportOrBlindRequestValidity(user, diaryId);
        Diary target = getDiaryById(diaryId);
        memberBlindDiaryRepository.save(new MemberBlindDiary(user, target));
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

    private void checkReportOrBlindRequestValidity(Member user, Long diaryId) {
        Diary diary = getDiaryById(diaryId);
        diary.checkNotWriter(user);
        diary.checkIsOpened();
    }
}
