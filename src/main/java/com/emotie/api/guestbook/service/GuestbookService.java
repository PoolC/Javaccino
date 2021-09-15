package com.emotie.api.guestbook.service;

import com.emotie.api.auth.exception.UnauthorizedException;
import com.emotie.api.guestbook.domain.Guestbook;
import com.emotie.api.guestbook.dto.GuestbookCreateRequest;
import com.emotie.api.guestbook.dto.GuestbookUpdateRequest;
import com.emotie.api.guestbook.exception.MyselfException;
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
        String ownerId = memberService.getMemberByNickname(nickname).getUUID();
        return guestbookRepository.findByOwnerId(ownerId);
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

    public Boolean toggleReport(Member user, Integer guestbookId) {
        checkToggleReportRequestValidity(user, guestbookId);

        Guestbook target = getGuestbookById(guestbookId);

        if (user.isReported(target)) {
            unreport(user, target);
            memberRepository.saveAndFlush(user);
            guestbookRepository.saveAndFlush(target);
            return false;
        }

        report(user, target);
        memberRepository.saveAndFlush(user);
        guestbookRepository.saveAndFlush(target);
        return true;
    }

    public void delete(Member executor, Integer guestbookId) {
        checkDeleteRequestValidity(executor, guestbookId);
        guestbookRepository.deleteById(guestbookId);
    }

    public void clear(Member user, String nickname) {
        checkClearRequestValidity(user, nickname);
        guestbookRepository.deleteByOwnerId(nickname);
    }

    public Boolean toggleBlind(Member user, Integer guestbookId) {
        checkBlindRequestValidity(user, guestbookId);

        Guestbook target = getGuestbookById(guestbookId);

        // TODO: 글 숨기기 논의 해결 후
        return true;
//        if (user.isBlinded(target)) {
//            unblind(user, target);
//            memberRepository.saveAndFlush(user);
//            guestbookRepository.saveAndFlush(target);
//            return false;
//        }
//
//        blind(user, target);
//        memberRepository.saveAndFlush(user);
//        guestbookRepository.saveAndFlush(target);
//        return true;
    }

    /*
    유효성 검사 메서드
     */
    private void checkGetAllBoardsRequestValidity(Member user, String nickname) {
        memberService.checkLogin(user);
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

    private void checkToggleReportRequestValidity(Member user, Integer guestbookId) {
        memberService.checkLogin(user);
        checkNotWriter(user, guestbookId);
    }

    private void checkDeleteRequestValidity(Member user, Integer guestbookId) {
        checkWriterOrOwner(user, guestbookId);
        checkNotReported(guestbookId);
    }

    private void checkClearRequestValidity(Member user, String nickname) {
        checkOwner(user, nickname);
        memberService.getMemberByNickname(nickname);
    }

    private void checkBlindRequestValidity(Member user, Integer guestbookId) {
        checkOwner(user, guestbookId);
    }

    /*
    기타 메서드
     */
    private Guestbook getGuestbookById(Integer guestbookId) {
        return guestbookRepository.findById(guestbookId).orElseThrow(() -> {
            throw new NoSuchElementException("해당 id를 가진 방명록이 없습니다.");
        });
    }

    private Member getWriterByGuestbook(Guestbook guestbook) {
        return memberService.getMemberById(guestbook.getWriterId());
    }

    private Member getOwnerByGuestbook(Guestbook guestbook) {
        return memberService.getMemberById(guestbook.getOwnerId());
    }

    // TODO: 자기자신을 팔로우할 수 없는 CannotFollowException과 합칠 수 있을까?
    private void checkNotOwner(Member user, String nickname) {
        if (user.equals(memberService.getMemberByNickname(nickname))) {
            throw new MyselfException("자신의 방명록에는 글을 쓸 수 없습니다.");
        }
    }

    private void checkWriter(Member user, Integer guestbookId) {
        Member writer = getWriterByGuestbook(getGuestbookById(guestbookId));
        if (!user.equals(writer)) {
            throw new UnauthorizedException("해당 방명록 글의 작성자가 아닙니다.");
        }
    }

    private void checkNotWriter(Member user, Integer guestbookId) {
        Member writer = getWriterByGuestbook(getGuestbookById(guestbookId));
        if (user.equals(writer)) {
            throw new MyselfException("자신이 작성한 방명록 글은 신고할 수 없습니다.");
        }
    }

    // TODO: 글 숨기기 논의 해결 후
    // TODO: 트랜잭션 안 겹치게
    private void report(Member user, Guestbook target) {
        user.report(target);
        target.reportedBy(user);
        getWriterByGuestbook(target).updateReportCount(true);
    }

    // TODO: 글 숨기기 논의 해결 후
    private void unreport(Member user, Guestbook target) {
        user.unreport(target);
        target.unreportedBy(user);
        getWriterByGuestbook(target).updateReportCount(false);
    }

    private void checkWriterOrOwner(Member user, Integer guestbookId) {
        Guestbook guestbook = getGuestbookById(guestbookId);
        Member writer = getWriterByGuestbook(guestbook);
        Member owner = getOwnerByGuestbook(guestbook);
        if (!(user.equals(writer) || user.equals(owner))) {
            throw new UnauthorizedException("방명록 게시물을 삭제할 권한이 없습니다.");
        }
    }

    // TODO: blinded 칼럼을 추가해서 관리하는게 더 나을까?
    private void checkNotReported(Integer guestbookId) {
        Guestbook guestbook = getGuestbookById(guestbookId);
        if (guestbook.isNotOverReported()) {
            throw new UnauthorizedException("신고를 많이 받아 삭제할 수 없는 방명록입니다.");
        }
    }

    private void checkOwner(Member user, String nickname) {
        Member owner = memberService.getMemberByNickname(nickname);
        if (!user.equals(owner)) {
            throw new UnauthorizedException("방명록 전체 삭제 권한이 없습니다.");
        }
    }

    // TODO: 트랜잭션 안 겹치게
    // TODO: 글 숨기기 논의 해결 후
    private void blind(Member user, Guestbook target) {
//        user.blind(target);
//        target.blindedBy(user);
    }

    // TODO: 글 숨기기 논의 해결 후
    private void unblind(Member user, Guestbook target) {
//        user.unblind(target);
//        target.unblindedBy(user);
    }

    private void checkOwner(Member user, Integer guestbookId) {
        Member owner = getOwnerByGuestbook(getGuestbookById(guestbookId));
        if (!user.equals(owner)) {
            throw new UnauthorizedException("방명록 게시물을 숨길 권한이 없습니다.");
        }
    }
}