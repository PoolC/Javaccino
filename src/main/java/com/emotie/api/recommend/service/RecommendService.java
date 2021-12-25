package com.emotie.api.recommend.service;

import com.emotie.api.emotion.domain.Emotion;
import com.emotie.api.emotion.domain.Emotions;
import com.emotie.api.emotion.domain.EmotionsComparator;
import com.emotie.api.emotion.repository.EmotionRepository;
import com.emotie.api.member.domain.Member;
import com.emotie.api.profile.dto.ProfileResponse;
import com.emotie.api.profile.service.ProfileService;
import com.emotie.api.recommend.dto.RecommendResponse;
import com.emotie.api.recommend.repository.RecommendRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendService {

    private final ProfileService profileService;
    private final RecommendRepository recommendRepository;
    private final EmotionRepository emotionRepository;

    private final Integer NUMBER_OF_SAMPLES = 100;
    private final Integer NUMBER_OF_RECOMMENDATIONS = 20;

    public RecommendResponse recommendProfilesToUser(Member user) {
        Emotions userEmotions = new Emotions(user, emotionRepository.findAllByMember(user));
        EmotionsComparator comparator = new EmotionsComparator(userEmotions);

        List<Member> randomMembers = getRandomMembers(NUMBER_OF_SAMPLES);
        List<ProfileResponse> recommendations =
                randomMembers.stream().map(
                        member -> new Emotions(member, emotionRepository.findAllByMember(member))
                ).sorted(comparator).limit(NUMBER_OF_RECOMMENDATIONS)
                        .map(Emotions::getMember)
                .map(
                        member -> profileService.getProfile(member, member.getUUID())
                ).collect(Collectors.toList());

        return RecommendResponse.builder().profiles(recommendations).build();
    }

    private List<Member> getRandomMembers(Integer count) {
        return recommendRepository.randomExtraction(Pageable.ofSize(count));
    }
}