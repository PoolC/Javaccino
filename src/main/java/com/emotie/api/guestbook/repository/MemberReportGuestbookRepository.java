package com.emotie.api.guestbook.repository;

import com.emotie.api.guestbook.domain.Guestbook;
import com.emotie.api.guestbook.domain.MemberReportGuestbook;
import com.emotie.api.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface MemberReportGuestbookRepository extends JpaRepository<MemberReportGuestbook, Long> {

    Optional<MemberReportGuestbook> findByMemberAndGuestbook(Member member, Guestbook guestbook);

    @Transactional
    void deleteAllByMember(Member member);

    @Transactional
    void deleteAllByGuestbook(Guestbook guestbook);
}
