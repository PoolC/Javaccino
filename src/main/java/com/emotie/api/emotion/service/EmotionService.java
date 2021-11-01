package com.emotie.api.emotion.service;

import com.emotie.api.emotion.domain.Emotion;
import com.emotie.api.emotion.domain.Emotions;
import com.emotie.api.emotion.repository.EmotionRepository;
import com.emotie.api.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmotionService {
    private static final String EMOTION_BASE_PACKAGE = "com.emotie.api.emotion";

    private final EmotionRepository emotionRepository;

    public void deepenEmotionScore(Member member, String emotionName) {
        Emotions emotions = new Emotions(getEmotionsByMember(member));

        emotions.deepenCurrentEmotionScore(emotionName);
        emotionRepository.saveAllAndFlush(emotions.allMemberEmotions());
    }

    public Emotion getEmotionByMemberAndEmotionName(Member member, String emotionName) {
        return emotionRepository.findByMemberAndName(member, emotionName)
                .orElseThrow(() -> new RuntimeException("emotion does not exist: " + emotionName));
    }

    private List<Emotion> getEmotionsByMember(Member member) {
        List<Emotion> emotions = emotionRepository.findAllByMember(member);
        if(noEmotionsYet(emotions)) {
            emotions = createNewEmotions(member);
        }

        return emotions;
    }

    private boolean noEmotionsYet(List<Emotion> emotions) {
        return emotions.size() == 0;
    }

    private List<Emotion> createNewEmotions(Member member) {
        return new Reflections(EMOTION_BASE_PACKAGE, new SubTypesScanner())
                .getSubTypesOf(Emotion.class).stream()
                .map(concreteEmotionClass -> {
                    try {
                        return concreteEmotionClass.getDeclaredConstructor(Member.class).newInstance(member);
                    } catch (Exception e) {
                        throw new RuntimeException("Couldn't create concrete Emotion class\n" + e.getMessage());
                    }
                })
                .collect(Collectors.toList());
    }
}
