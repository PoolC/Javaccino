package com.emotie.api.guestbook.service;

import com.emotie.api.auth.exception.UnauthorizedException;
import com.emotie.api.guestbook.domain.Guestbook;
import com.emotie.api.guestbook.dto.GuestbookCreateRequest;
import com.emotie.api.guestbook.dto.GuestbookUpdateRequest;
import com.emotie.api.guestbook.repository.GuestbookRepository;
import com.emotie.api.member.domain.Member;
import com.emotie.api.member.repository.MemberRepository;
import com.emotie.api.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class GuestbookService {
    private final MemberRepository memberRepository;
    private final GuestbookRepository guestbookRepository;
    private final MemberService memberService;

    public List<Guestbook> getAllBoards(Member user, String nickname) {
        checkGetAllBoardsRequestValidity(user, nickname);
        return guestbookRepository.findByNickname(nickname);
    }

    public void create(Member user, GuestbookCreateRequest request, String nickname) {
        checkCreateRequestValidity(user, nickname);
        guestbookRepository.save(
                Guestbook.builder()
                        .ownerId(memberService.getMemberByNickname(nickname).getUUID())
                        .writerId(user.getUUID())
                        .content(request.getContent())
                        .reportCount(0)
                        .build()
        );
    }

    public void update(Member user, GuestbookUpdateRequest request, Integer guestbookId) {
        checkUpdateRequestValidity(user, guestbookId);
        Guestbook guestbook = getGuestbookById(guestbookId);
        guestbook.update(request);
        guestbookRepository.saveAndFlush(guestbook);
    }

    public Boolean toggleReport(Member user, String nickname) {
        
    }

    /*
    유효성 검사 메서드
     */
    private void checkGetAllBoardsRequestValidity(Member user, String nickname) {
        memberService.checkLogin(user);
        memberService.getMemberByNickname(nickname);
    }

    private void checkCreateRequestValidity(Member user, String nickname) {
        memberService.checkLogin(user);
        checkNotOwner(user, nickname);
        memberService.getMemberByNickname(nickname);
    }

    private void checkUpdateRequestValidity(Member user, Integer guestbookId) {
        memberService.checkLogin(user);
        checkWriter(user, guestbookId);
    }

    /*
    기타 메서드
     */

    private Guestbook getGuestbookById(Integer guestbookId) {
        return guestbookRepository.findById(guestbookId).orElseThrow(() -> {
            throw new NoSuchElementException("해당 id를 가진 방명록이 없습니다.");
        });
    }

    // TODO: 자기자신을 팔로우할 수 없는 CannotFollowException과 합칠 수 있을까?
    private void checkNotOwner(Member user, String nickname) {
        if (user.equals(memberService.getMemberByNickname(nickname)))
            throw new UnauthorizedException("자신의 방명록에는 글을 쓸 수 없습니다.");
    }

    private void checkWriter(Member user, Integer guestbookId) {
        Guestbook guestbook = getGuestbookById(guestbookId);
        Member writer = memberService.getMemberById(guestbook.getWriterId());
        if (!user.equals(writer))
            throw new UnauthorizedException("해당 방명록 글의 작성자가 아닙니다.");
    }
}
