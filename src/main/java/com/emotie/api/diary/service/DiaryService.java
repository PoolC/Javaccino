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
import com.emotie.api.member.domain.Follow;
import com.emotie.api.emotion.service.EmotionService;
import com.emotie.api.member.domain.Member;
import com.emotie.api.member.repository.FollowRepository;
import com.emotie.api.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import org.springframework.data.domain.Pageable;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
@Service
@RequiredArgsConstructor
public class DiaryService {
    private static final int PAGE_SIZE = 10;

    private final DiaryRepository diaryRepository;
    private final EmotionRepository emotionRepository;

    private final MemberReportDiaryRepository memberReportDiaryRepository;
    private final MemberBlindDiaryRepository memberBlindDiaryRepository;
    private final MemberRepository memberRepository;
    private final FollowRepository followRepository;

    private final EmotionService emotionService;

    @Transactional
    public void create(Member member, DiaryCreateRequest request) {
        Emotion emotion = getEmotionByEmotion(request.getEmotion());
        diaryRepository.save(
                Diary.builder()
                        .writer(member)
                        .emotion(emotion)
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

        diary.checkUserValidity(user);

        Emotion updatingEmotion = getEmotionByEmotion(request.getEmotion());

        updateDiaryWithRequest(diary, request);
        diaryRepository.saveAndFlush(diary);
        emotionRepository.saveAndFlush(originalEmotion);
        emotionRepository.saveAndFlush(updatingEmotion);

        return originalEmotion.getEmotion();
    }

    public List<String> delete(Member user, DiaryDeleteRequest request) {
        Set<Long> id = new HashSet<>(request.getDiaryId());
        checkDeleteListValidity(user, id);
        LinkedList<String> emotions = new LinkedList<>();
        id.stream().map(this::getDiaryById).forEach(
                (diary) -> {
                    emotions.add(diary.getEmotion().getEmotion());
                    diaryRepository.delete(diary);
                }
        );

        return emotions;
    }

    public DiaryReadAllResponse getFeed(Member user, Integer page) {
        List<Follow> followingMember = followRepository.findByFromMember(user);
        List<Diary> feed  = new LinkedList<>();
        followingMember.stream().forEach(follow -> {
            List<Diary> diaries = diaryRepository.findAllByWriterAndIsOpened(follow.getToMember(), true);
            feed.addAll(diaries);
        });
        feed.sort(Comparator.comparing(Diary::getCreatedAt).reversed());
        List<DiaryReadResponse> collect = feed.stream().map(DiaryReadResponse::new).skip(page*5).limit(5).collect(Collectors.toList());
        return new DiaryReadAllResponse(collect);
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

    private Member getMemberById(String memberId) {
        return memberRepository.findById(memberId).orElseThrow(
                () -> new NoSuchElementException("해당하는 아이디의 멤버가 없습니다.")
        );
    }

    private Emotion getEmotionByEmotion(String emotion) {
        return emotionRepository.findByEmotion(emotion).orElseThrow(
                () -> new NoSuchElementException("해당하는 이름의 감정이 없습니다.")
        );
    }

    private void updateDiaryWithRequest(Diary diary, DiaryUpdateRequest updateRequest) {
        diary.rewriteContent(updateRequest.getContent());
        diary.updateEmotion(getEmotionByEmotion(updateRequest.getEmotion()));
        diary.updateOpenness(updateRequest.getIsOpened());
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
