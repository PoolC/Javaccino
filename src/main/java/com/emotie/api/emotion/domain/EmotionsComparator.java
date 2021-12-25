package com.emotie.api.emotion.domain;

import java.util.Comparator;

public class EmotionsComparator implements Comparator<Emotions> {
    private final Emotions anchorEmotions;

    private final Double EUCLIDEAN_DISTANCE_WEIGHT = 0.3;

    public EmotionsComparator(Emotions anchorEmotions) {
        this.anchorEmotions = anchorEmotions;
    }

    @Override
    public int compare(Emotions o1, Emotions o2) {
        Double o1Score = recommendationScore(o1);
        Double o2Score = recommendationScore(o2);
        return o1Score.compareTo(o2Score);
    }

    private Double recommendationScore(Emotions o) {
        return EUCLIDEAN_DISTANCE_WEIGHT * anchorEmotions.computeEuclideanDistance(o) +
                (1 - EUCLIDEAN_DISTANCE_WEIGHT) * anchorEmotions.computeCosineSimilarity(o);
    }
}
