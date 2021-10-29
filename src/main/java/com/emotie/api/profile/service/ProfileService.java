package com.emotie.api.profile.service;

import com.emotie.api.emotion.repository.EmotionRepository;
import com.emotie.api.member.domain.Member;
import com.emotie.api.member.repository.FollowRepository;
import com.emotie.api.member.repository.MemberRepository;
import com.emotie.api.member.service.MemberService;
import com.emotie.api.profile.dto.FolloweeResponse;
import com.emotie.api.profile.dto.FollowerResponse;
import com.emotie.api.profile.dto.ProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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





        return ProfileResponse.builder()
                .nickname(profileMember.getNickname())
                .introduction(profileMember.getIntroduction())
                .followed(followed)
                .followers(followers)
                .followees(followees)
                .build();
    }



}
