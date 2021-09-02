package com.emotie.api.guestbook.repository;

import com.emotie.api.guestbook.domain.Guestbook;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GuestbookRepository extends JpaRepository<Guestbook, String> {

    List<Guestbook> findByOwnerId(String ownerId);

    Optional<Guestbook> findById(Integer guestbookId);

    void deleteById(Integer guestbookId);

    void deleteByOwnerId(String nickname);
}
