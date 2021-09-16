package com.emotie.api.guestbook.repository;

import com.emotie.api.guestbook.domain.Guestbook;
import com.emotie.api.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface GuestbookRepository extends JpaRepository<Guestbook, Integer> {

    List<Guestbook> findByOwner(Member owner);

    Optional<Guestbook> findById(Integer guestbookId);

    void deleteAllByOwner(Member owner);

    void deleteById(Integer guestbookId);
}
