package com.emotie.api.profile.service;

import com.emotie.api.diary.domain.Diary;
import com.emotie.api.diary.repository.DiaryRepository;
import com.emotie.api.emotion.domain.Emotion;
import com.emotie.api.emotion.dto.EmotionResponse;
import com.emotie.api.emotion.dto.EmotionsResponse;
import com.emotie.api.member.domain.EmotionScore;
import com.emotie.api.member.domain.Member;
import com.emotie.api.member.repository.MemberRepository;
import com.emotie.api.member.service.MemberService;
import com.emotie.api.profile.dto.FolloweeResponse;
import com.emotie.api.profile.dto.FollowerResponse;
import com.emotie.api.profile.dto.ProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final MemberService memberService;
    private final DiaryRepository diaryRepository;
    private final MemberRepository memberRepository;

    public ProfileResponse getProfile(Member member, String memberId){
        Member profileMember = memberService.getMemberById(memberId);
        Boolean followed = memberService.isFollowed(member, profileMember);
        List<FollowerResponse> followers = memberService.getFollowersByMember(profileMember);
        List<FolloweeResponse> followees = memberService.getFolloweesByMember(profileMember);
        List<EmotionResponse> allEmotion = getAllEmotion(profileMember);
        List<EmotionResponse> recentEmotion = getRecentEmotion(profileMember).stream().map(EmotionResponse::new).collect(Collectors.toList());

        return ProfileResponse.builder()
                .nickname(profileMember.getNickname())
                .introduction(profileMember.getIntroduction())
                .allEmotion(new EmotionsResponse(allEmotion))
                .recentEmotion(new EmotionsResponse(recentEmotion))
                .followed(followed)
                .followers(followers)
                .followees(followees)
                .build();
    }

    public void updateProfile(Member member, String updatingIntroduction){
        member.updateIntroduction(updatingIntroduction);
        memberRepository.save(member);
    }

    private List<EmotionResponse> getAllEmotion(Member profileMember) {
        Map<Emotion, EmotionScore> emotionScore = new TreeMap<>(profileMember.getEmotionScore());

        List<EmotionResponse> allEmotion = new ArrayList<>();
        for (Entry<Emotion, EmotionScore> entry: emotionScore.entrySet()) {
            if(entry.getValue().getScore() > 0) {
                allEmotion.add(new EmotionResponse(entry.getKey()));
            }
        }
        return allEmotion;
    }

    private List<Emotion> getRecentEmotion(Member profileMember) {
        List<Emotion> recentEmotion = new ArrayList<>();
        PageRequest pageRequest = PageRequest.of(0, 100, Sort.by("createdAt").descending());
        List<Diary> diaries = diaryRepository.findByWriter(profileMember, pageRequest);
        for (Diary diary : diaries){
            if(recentEmotion.size() == 2){
                break;
            }
            if(!recentEmotion.contains(diary.getEmotion())){
                recentEmotion.add(diary.getEmotion());
            }
        }
        return recentEmotion;
    }
}
