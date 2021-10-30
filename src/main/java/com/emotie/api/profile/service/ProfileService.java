package com.emotie.api.profile.service;

import com.emotie.api.emotion.domain.Emotion;
import com.emotie.api.emotion.dto.EmotionResponse;
import com.emotie.api.emotion.dto.EmotionsResponse;
import com.emotie.api.emotion.repository.EmotionRepository;
import com.emotie.api.member.domain.EmotionScore;
import com.emotie.api.member.domain.Member;
import com.emotie.api.member.repository.FollowRepository;
import com.emotie.api.member.repository.MemberRepository;
import com.emotie.api.member.service.MemberService;
import com.emotie.api.profile.dto.FolloweeResponse;
import com.emotie.api.profile.dto.FollowerResponse;
import com.emotie.api.profile.dto.ProfileResponse;
import groovyjarjarantlr4.runtime.tree.Tree;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static com.emotie.api.common.init.EmotionProvider.totalEmotionNumbers;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final MemberRepository memberRepository;
    private final FollowRepository followRepository;
    private final EmotionRepository emotionRepository;
    private final MemberService memberService;

    public ProfileResponse getProfile(Member member, String memberId){


        Member profileMember = memberService.getMemberById(memberId);
        Boolean followed = followRepository.findFollowByFromMemberAndToMember(member, profileMember).isPresent();
        List<FollowerResponse> followers = followRepository.findFollowByToMember(profileMember).get().stream().map(FollowerResponse::new).collect(Collectors.toList());
        List<FolloweeResponse> followees = followRepository.findFollowByFromMember(profileMember).get().stream().map(FolloweeResponse::new).collect(Collectors.toList());


        Map<Emotion, EmotionScore> emotionScore = new TreeMap<>(profileMember.getEmotionScore());



        List<EmotionResponse> allEmotion = new ArrayList<>();
        for (Emotion emotion : emotionScore.keySet()) {
            allEmotion.add(new EmotionResponse(emotion));
        }


        return ProfileResponse.builder()
                .nickname(profileMember.getNickname())
                .introduction(profileMember.getIntroduction())
                .allEmotion(new EmotionsResponse(allEmotion))
                .recentEmotion(null)
                .followed(followed)
                .followers(followers)
                .followees(followees)
                .build();
    }



}
