package com.emotie.api.guestbook.repository;

import com.emotie.api.guestbook.domain.Guestbook;
import com.emotie.api.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface GuestbookRepository extends JpaRepository<Guestbook, Integer> {

    List<Guestbook> findByOwner(Member owner);

    Optional<Guestbook> findById(Integer guestbookId);

    @Query(value = "SELECT g FROM guestbooks g WHERE g.owner = :owner AND g.reportCount < :reportCountThreshold AND g.isGlobalBlinded = false")
    List<Guestbook> findForUserByOwner(@Param("owner") Member owner, @Param("reportCountThreshold") Integer reportCountThreshold);

    @Query(value = "SELECT g FROM guestbooks g WHERE g.owner = :owner AND g.reportCount < :reportCountThreshold")
    List<Guestbook> findForOwnerByOwner(@Param("owner") Member owner, @Param("reportCountThreshold") Integer reportCountThreshold);

    @Transactional
    void deleteAllByOwner(Member owner);

    @Transactional
    void deleteById(Integer guestbookId);
}
