package com.emotie.api.emotion.service;

import com.emotie.api.emotion.domain.Emotion;
import com.emotie.api.emotion.domain.Emotions;
import com.emotie.api.emotion.repository.EmotionRepository;
import com.emotie.api.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EmotionService {
    private final EmotionRepository emotionRepository;

    @Transactional
    public void deepenEmotionScore(Member member, String emotionName) {
        Emotions emotions = new Emotions(member, emotionRepository.findAllByMember(member));

        emotions.deepenCurrentEmotionScore(emotionName);
        emotionRepository.saveAllAndFlush(emotions.allMemberEmotions());
    }

    public void reduceEmotionScore(Member member, String emotionName) {
        Emotions emotions = new Emotions(member, emotionRepository.findAllByMember(member));

        emotions.reduceCurrentEmotionScore(emotionName);
        emotionRepository.saveAllAndFlush(emotions.allMemberEmotions());
    }

    public Emotion getEmotionByMemberAndEmotionName(Member member, String emotionName) {
        return emotionRepository.findByMemberAndName(member, emotionName)
                .orElseThrow(() -> new RuntimeException("emotion does not exist: " + emotionName));
    }
}
