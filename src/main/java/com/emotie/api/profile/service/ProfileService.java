package com.emotie.api.profile.service;

import com.emotie.api.diary.domain.Diary;
import com.emotie.api.diary.repository.DiaryRepository;
import com.emotie.api.emotion.domain.Emotion;
import com.emotie.api.emotion.domain.Emotions;
import com.emotie.api.emotion.dto.EmotionResponse;
import com.emotie.api.emotion.dto.EmotionsResponse;
import com.emotie.api.emotion.repository.EmotionRepository;
import com.emotie.api.member.domain.Member;
import com.emotie.api.member.repository.MemberRepository;
import com.emotie.api.member.service.MemberService;
import com.emotie.api.profile.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final MemberService memberService;
    private final DiaryRepository diaryRepository;
    private final MemberRepository memberRepository;
    private final EmotionRepository emotionRepository;

    public ProfileCardResponse getProfileCard(Member member, String memberId){
        Member profileMember = memberService.getMemberById(memberId);
        Emotion allEmotion = getAllEmotion(profileMember);
        List<EmotionResponse> recentEmotion = getRecentEmotion(profileMember).stream().map(EmotionResponse::new).collect(Collectors.toList());
        return ProfileCardResponse.builder()
                .nickname(profileMember.getNickname())
                .introduction(profileMember.getIntroduction())
                .allEmotion(new EmotionResponse(allEmotion))
                .recentEmotion(recentEmotion)
                .characterName(profileMember.getCharacterName())
                .memberId(memberId)
                .build();
    }

    public ProfileResponse getProfile(Member member, String memberId){
        Member profileMember = memberService.getMemberById(memberId);
        Boolean followed = memberService.isFollowed(member, profileMember);
        List<FollowerResponse> followers = memberService.getFollowersByMember(profileMember);
        List<FolloweeResponse> followees = memberService.getFolloweesByMember(profileMember);
        Emotion allEmotion = getAllEmotion(profileMember);
        List<EmotionResponse> recentEmotion = getRecentEmotion(profileMember).stream().map(EmotionResponse::new).collect(Collectors.toList());

        return ProfileResponse.builder()
                .nickname(profileMember.getNickname())
                .introduction(profileMember.getIntroduction())
                .allEmotion(new EmotionResponse(allEmotion))
                .recentEmotion(recentEmotion)
                .followed(followed)
                .followers(followers)
                .followees(followees)
                .characterName(profileMember.getCharacterName())
                .memberId(memberId)
                .build();
    }

    public void updateProfile(Member member, ProfileUpdateRequest profileUpdateRequest){
        member.updateProfile(profileUpdateRequest);
        memberRepository.save(member);
    }

    public List<Emotion> getRecentEmotion(Member profileMember) {
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

    private Emotion getAllEmotion(Member profileMember) {
        Emotions emotions = new Emotions(profileMember, emotionRepository.findAllByMember(profileMember));
        Comparator<Emotion> comparatorByEmotionScore = Comparator.comparingDouble(Emotion::getScore);
        return emotions.allMemberEmotions().stream().max(comparatorByEmotionScore).get();

    }
}
