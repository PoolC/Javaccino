package com.emotie.api.diary.repository;

import com.emotie.api.diary.domain.Diary;
import com.emotie.api.emotion.domain.Emotion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DiaryRepository extends JpaRepository<Diary, Integer> {
    List<Diary> findByEmotion(Emotion Emotion);
//    List<Diaries> findAfterDate(Date startDate);
//
//    List<Diaries> findBeforeDate(Date endDate);
//
//    List<Diaries> findByEmotionId(Integer emotionId);
//
//    List<Diaries> findByKeywords(List<String> keywords);
//
//    List<Diaries> findExceptKeywords(List<String> keywords);
//
//    List<Diaries> findByOpenness(Boolean openness);
}
