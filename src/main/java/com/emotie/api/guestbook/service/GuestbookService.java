package com.emotie.api.guestbook.service;

import com.emotie.api.guestbook.domain.Guestbook;
import com.emotie.api.guestbook.repository.GuestbookRepository;
import com.emotie.api.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GuestbookService {
    private final MemberRepository memberRepository;
    private final GuestbookRepository guestbookRepository;

    public List<Guestbook> getAllBoards(String nickname) {
        return guestbookRepository.findByNickname(nickname);
    }


}
