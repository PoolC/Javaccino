package com.emotie.api.guestbook.repository;

import com.emotie.api.guestbook.domain.Guestbook;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GuestbookRepository extends JpaRepository<Guestbook, String> {

    List<Guestbook> findByNickname(String nickname);

    Optional<Guestbook> findById(Integer guestbookId);
}
