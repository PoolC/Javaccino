package com.emotie.api.diary.repository;

import com.emotie.api.diary.domain.Diary;
import com.emotie.api.diary.domain.MemberReportDiary;
import com.emotie.api.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface MemberReportDiaryRepository extends JpaRepository<MemberReportDiary, Long> {

    Optional<MemberReportDiary> findByMemberAndDiary(Member member, Diary diary);

    @Transactional
    void deleteAllByMember(Member member);

    @Transactional
    void deleteAllByDiary(Diary diary);
}
