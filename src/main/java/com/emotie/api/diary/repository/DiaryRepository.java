package com.emotie.api.diary.repository;

import com.emotie.api.diary.domain.Diary;
import com.emotie.api.emotion.domain.Emotion;
import com.emotie.api.member.domain.Member;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DiaryRepository extends JpaRepository<Diary, Long> {

    List<Diary> findByEmotion(Emotion Emotion);

    List<Diary> findByWriter(Member writer, Pageable pageable);

    // TODO: 중복되는 쿼리 부분 코드가 너무 긴데 간결화 가능?
    @Query(value = "SELECT d" +
            " FROM emodiaries d" +
            " WHERE d.writer = :writer" +
            " AND d.isOpened = :isOpened" +
            " AND d.id NOT IN (SELECT DISTINCT mrd.diary FROM members_report_diaries mrd WHERE mrd.member = :user)" +
            " AND d.id NOT IN (SELECT DISTINCT mbd.diary FROM members_blind_diaries mbd WHERE mbd.member = :user)" +
            " AND d.reportCount < :reportCountThreshold")
    List<Diary> findAllByWriterAndIsOpenedAndNotBlinded(Member user, Member writer, Boolean isOpened, Integer reportCountThreshold);

    @Query(value = "SELECT d" +
            " FROM emodiaries d" +
            " WHERE d.writer = :writer" +
            " AND d.id NOT IN (SELECT DISTINCT mrd.diary FROM members_report_diaries mrd WHERE mrd.member = :user)" +
            " AND d.id NOT IN (SELECT DISTINCT mbd.diary FROM members_blind_diaries mbd WHERE mbd.member = :user)" +
            " AND d.reportCount < :reportCountThreshold")
    List<Diary> findAllByWriterAndNotBlinded(Member user, Member writer, Integer reportCountThreshold, Pageable pageable);

    @Query(value = "SELECT d" +
            " FROM emodiaries d" +
            " WHERE d.writer = :writer" +
            " AND d.isOpened = :isOpened" +
            " AND d.id NOT IN (SELECT DISTINCT mrd.diary FROM members_report_diaries mrd WHERE mrd.member = :user)" +
            " AND d.id NOT IN (SELECT DISTINCT mbd.diary FROM members_blind_diaries mbd WHERE mbd.member = :user)" +
            " AND d.reportCount < :reportCountThreshold")
    List<Diary> findAllByWriterAndIsOpenedAndNotBlinded(Member user, Member writer, Boolean isOpened, Integer reportCountThreshold, Pageable pageable);
}
