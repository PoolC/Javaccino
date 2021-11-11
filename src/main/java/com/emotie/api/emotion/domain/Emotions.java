package com.emotie.api.emotion.domain;

import com.emotie.api.member.domain.Member;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class Emotions {
    private static final String EMOTION_BASE_PACKAGE = "com.emotie.api.emotion";
    private static final Integer REDUCE_AMOUNT = 1;
    private static final Integer NOT_REDUCE_AMOUNT = 0;
    private static final Integer DEEPEN_AMOUNT = 1;
    private static final Integer NOT_DEEPEN_AMOUNT = 0;

    private final List<Emotion> emotions;

    public Emotions(Member member, List<Emotion> emotions) {
        if(noEmotionsYet(emotions)) {
            emotions = new Reflections(EMOTION_BASE_PACKAGE, new SubTypesScanner())
                    .getSubTypesOf(Emotion.class).stream()
                    .map(concreteEmotionClass -> {
                        try {
                            return concreteEmotionClass.getDeclaredConstructor(Member.class).newInstance(member);
                        } catch (Exception e) {
                            throw new NoSuchElementException("Couldn't create concrete Emotion class\n" + e.getMessage());
                        }
                    })
                    .collect(Collectors.toList());
        }
        this.emotions = Collections.unmodifiableList(emotions);
    }

    public List<Emotion> allMemberEmotions() {
        return this.emotions;
    }

    public void reduceCurrentEmotionScore(String emotionName) {
        reduceScore(emotionName);
        notReduceEmotionScoresWithoutEmotion(emotionName);
    }

    public void deepenCurrentEmotionScore(String emotionName) {
        deepenScore(emotionName);
        notDeepenEmotionScoresWithoutEmotion(emotionName);
    }

    private void notReduceEmotionScoresWithoutEmotion(String excludeEmotionName) {
        validateAmount(NOT_REDUCE_AMOUNT);

        emotions.stream()
                .filter(emotion -> !emotion.getName().equals(excludeEmotionName))
                .forEach(emotion -> emotion.reduceScore(NOT_REDUCE_AMOUNT));
    }

    private void notDeepenEmotionScoresWithoutEmotion(String excludeEmotionName) {
        validateAmount(NOT_DEEPEN_AMOUNT);

        emotions.stream()
                .filter(emotion -> !emotion.getName().equals(excludeEmotionName))
                .forEach(emotion -> emotion.deepenScore(NOT_DEEPEN_AMOUNT));
    }

    private void deepenScore(String deepenEmotionName) {
        validateAmount(DEEPEN_AMOUNT);

        emotions.stream()
                .filter(emotion -> emotion.getName().equals(deepenEmotionName))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException(
                        deepenEmotionName + " could not be found. This exception is impossible"))
                .deepenScore(DEEPEN_AMOUNT);
    }

    private void reduceScore(String reduceEmotionName) {
        validateAmount(REDUCE_AMOUNT);

        emotions.stream()
                .filter(emotion -> emotion.getName().equals(reduceEmotionName))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException(
                        reduceEmotionName + " could not be found. This exception is impossible"))
                .reduceScore(REDUCE_AMOUNT);
    }

    private void validateAmount(Integer score) {
        if(!acceptedScoreRange(score)) {
            throw new ArithmeticException("Score can only be 0 or 1");
        }
    }

    private boolean acceptedScoreRange(Integer score) {
        return (score == 1) || (score == 0);
    }

    private boolean noEmotionsYet(List<Emotion> emotions) {
        return emotions.size() == 0;
    }
}
