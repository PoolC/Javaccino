package com.emotie.api.diaries.repository;

import com.emotie.api.diaries.domain.Diaries;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface DiariesRepository extends JpaRepository<Diaries, Integer> {
    List<Diaries> findAfterDate(Date startDate);

    List<Diaries> findBeforeDate(Date endDate);

    List<Diaries> findByEmotionId(Integer emotionId);

    List<Diaries> findByKeywords(List<String> keywords);

    List<Diaries> findExceptKeywords(List<String> keywords);

    List<Diaries> findByOpenness(Boolean openness);
}
