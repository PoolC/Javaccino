package com.emotie.api.diary.repository;

import com.emotie.api.diary.domain.Diary;
import com.emotie.api.emotion.domain.Emotion;
import com.emotie.api.member.domain.Member;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DiaryRepository extends JpaRepository<Diary, Long> {
    List<Diary> findByEmotion(Emotion Emotion);

    List<Diary> findAllByWriter(Member writer, Pageable pageable);

    List<Diary> findAllByWriterAndIsOpened(Member writer, Boolean isOpened, Pageable pageable);
}
