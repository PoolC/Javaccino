package com.emotie.api.guestbook.repository;

import com.emotie.api.guestbook.domain.Guestbook;
import com.emotie.api.member.domain.Member;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface GuestbookRepository extends JpaRepository<Guestbook, Long> {

    List<Guestbook> findByOwner(Member owner);

    Optional<Guestbook> findById(Long guestbookId);

    @Query(value = "SELECT g" +
            " FROM guestbooks g" +
            " WHERE g.owner = :owner" +
            " AND g.id NOT IN (SELECT DISTINCT mrg.guestbook FROM members_report_guestbooks mrg WHERE mrg.member = :user)" +
            " AND g.reportCount < :reportCountThreshold" +
            " AND g.isOwnerReported = false")
    List<Guestbook> findByOwner(@Param("user") Member user, @Param("owner") Member owner, @Param("reportCountThreshold") Integer reportCountThreshold, Pageable pageable);

    @Transactional
    void deleteById(Long guestbookId);
}
