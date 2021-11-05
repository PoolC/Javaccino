package com.emotie.api.member.repository;

import com.emotie.api.emotion.domain.Emotion;
import com.emotie.api.member.domain.EmotionScore;
import com.emotie.api.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmotionScoreRepository extends JpaRepository<EmotionScore, Integer> {
    Optional<EmotionScore> findByMemberIdAndEmotion(String memberId, Emotion emotion);
}
