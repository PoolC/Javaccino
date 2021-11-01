package com.emotie.api.emotion.domain;

import java.util.Collections;
import java.util.List;

public class Emotions {
    private static final Double REDUCE_AMOUNT = 1.0;
    private static final Double NOT_REDUCE_AMOUNT = 0.0;
    private static final Double DEEPEN_AMOUNT = 1.0;
    private static final Double NOT_DEEPEN_AMOUNT = 0.0;

    private final List<Emotion> emotions;

    // TODO: 감정이 8개였다가 변하면 오류나서 감정계산할때 전체 재계산이 필요한지 여부 파악 필요
    public Emotions(List<Emotion> emotions) {
        this.emotions = Collections.unmodifiableList(emotions);
    }

    public List<Emotion> allMemberEmotions() {
        return this.emotions;
    }

    public void reduceCurrentEmotionScore(String emotionName) {
        reduceScore(emotionName, REDUCE_AMOUNT);
        notReduceEmotionScoresWithoutEmotion(emotionName, NOT_REDUCE_AMOUNT);
    }

    public void deepenCurrentEmotionScore(String emotionName) {
        deepenScore(emotionName, DEEPEN_AMOUNT);
        notDeepenEmotionScoresWithoutEmotion(emotionName, NOT_DEEPEN_AMOUNT);
    }

    private void notReduceEmotionScoresWithoutEmotion(String excludeEmotionName, Double notReduceAmount) {
        validateAmount(notReduceAmount);

        emotions.stream()
                .filter(emotion -> !emotion.getName().equals(excludeEmotionName))
                .forEach(emotion -> emotion.reduceScore(notReduceAmount));
    }

    private void notDeepenEmotionScoresWithoutEmotion(String excludeEmotionName, Double notDeepenAmount) {
        validateAmount(notDeepenAmount);

        emotions.stream()
                .filter(emotion -> !emotion.getName().equals(excludeEmotionName))
                .forEach(emotion -> emotion.deepenScore(notDeepenAmount));
    }

    private void deepenScore(String deepenEmotionName, Double deepenAmount) {
        validateAmount(deepenAmount);

        emotions.stream()
                .filter(emotion -> emotion.getName().equals(deepenEmotionName))
                .findFirst()
                .orElseThrow(() -> new RuntimeException(
                        deepenEmotionName + " could not be found. This exception is impossible"))
                .deepenScore(deepenAmount);
    }

    private void reduceScore(String reduceEmotionName, Double reduceAmount) {
        validateAmount(reduceAmount);

        emotions.stream()
                .filter(emotion -> emotion.getName().equals(reduceEmotionName))
                .findFirst()
                .orElseThrow(() -> new RuntimeException(
                        reduceEmotionName + " could not be found. This exception is impossible"))
                .deepenScore(reduceAmount);
    }

    private void validateAmount(Double score) {
        if(!acceptedScoreRange(score)) {
            throw new RuntimeException("Score can only be between 0.0 <= score <= 1.0");
        }
    }

    private boolean acceptedScoreRange(Double score) {
        return 0.0 <= score && score <= 1.0;
    }
}
