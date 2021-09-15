package com.emotie.api.guestbook.service;

import com.emotie.api.auth.exception.UnauthorizedException;
import com.emotie.api.guestbook.domain.Guestbook;
import com.emotie.api.guestbook.domain.MemberLocalBlindGuestbook;
import com.emotie.api.guestbook.domain.MemberReportGuestbook;
import com.emotie.api.guestbook.dto.GuestbookCreateRequest;
import com.emotie.api.guestbook.dto.GuestbookUpdateRequest;
import com.emotie.api.guestbook.exception.MyselfException;
import com.emotie.api.guestbook.repository.GuestbookRepository;
import com.emotie.api.member.domain.Member;
import com.emotie.api.member.repository.MemberRepository;
import com.emotie.api.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        Member owner = memberService.getMemberByNickname(nickname);
        return guestbookRepository.findByOwner(owner);
    }

    public void create(Member user, GuestbookCreateRequest request, String nickname) {
        checkCreateRequestValidity(user, request, nickname);
        Member owner = memberService.getMemberByNickname(nickname);
        guestbookRepository.save(
                Guestbook.builder()
                        .owner(owner)
                        .writer(user)
                        .content(request.getContent())
                        .reportCount(0)
                        .isGlobalBlinded(false)
                        .build()
        );
    }

    public void update(Member user, GuestbookUpdateRequest request, Integer guestbookId) {
        checkUpdateRequestValidity(user, request, guestbookId);
        Guestbook guestbook = getGuestbookById(guestbookId);
        guestbook.update(request);
        guestbookRepository.saveAndFlush(guestbook);
    }

    // TODO: isReported 잘 동작하는지 확인
    public Boolean toggleReport(Member user, Integer guestbookId) {
        checkToggleReportRequestValidity(user, guestbookId);
        Guestbook target = getGuestbookById(guestbookId);
        MemberReportGuestbook memberReportGuestbook = new MemberReportGuestbook(user, target);
        report(user, target);
        memberRepository.saveAndFlush(user);
        guestbookRepository.saveAndFlush(target);
        return user.isReportExists(memberReportGuestbook);
    }

    public void delete(Member executor, Integer guestbookId) {
        checkDeleteRequestValidity(executor, guestbookId);
        guestbookRepository.deleteById(guestbookId);
    }

    public void clear(Member user, String nickname) {
        checkClearRequestValidity(user, nickname);
        Member owner = memberService.getMemberByNickname(nickname);
        guestbookRepository.deleteByOwner(owner);
    }

    public Boolean toggleGlobalBlind(Member user, Integer guestbookId) {
        checkToggleGlobalBlindRequestValidity(user, guestbookId);
        Guestbook target = getGuestbookById(guestbookId);
        target.globalBlind();
        guestbookRepository.saveAndFlush(target);
        return target.getIsGlobalBlinded();
    }

    public Boolean toggleLocalBlind(Member user, Integer guestbookId) {
        checkToggleLocalBlindRequestValidity(user, guestbookId);
        Guestbook target = getGuestbookById(guestbookId);
        MemberLocalBlindGuestbook memberLocalBlindGuestbook = new MemberLocalBlindGuestbook(user, target);
        localBlind(user, target);
        memberRepository.saveAndFlush(user);
        guestbookRepository.saveAndFlush(target);
        return user.isLocalBlindExists(memberLocalBlindGuestbook);
    }

    /*
    유효성 검사 메서드
    (content null 여부는 Request 검증에서 체크)
     */
    private void checkGetAllBoardsRequestValidity(Member user, String nickname) {
        memberService.checkLogin(user);
        checkNicknameExists(nickname);
    }

    private void checkCreateRequestValidity(Member user, GuestbookCreateRequest request, String nickname) {
        memberService.checkLogin(user);
        checkNicknameExists(nickname);
        checkNotOwner(user, nickname);
    }

    private void checkUpdateRequestValidity(Member user, GuestbookUpdateRequest request, Integer guestbookId) {
        memberService.checkLogin(user);
        checkGuestbookIdExists(guestbookId);
        checkWriter(user, guestbookId);
        checkNotOverReported(guestbookId);
    }

    private void checkToggleReportRequestValidity(Member user, Integer guestbookId) {
        memberService.checkLogin(user);
        checkGuestbookIdExists(guestbookId);
        checkNotWriter(user, guestbookId);
    }

    private void checkDeleteRequestValidity(Member user, Integer guestbookId) {
        memberService.checkLogin(user);
        checkGuestbookIdExists(guestbookId);
        checkWriterOrOwner(user, guestbookId);
        checkNotOverReported(guestbookId);
    }

    private void checkClearRequestValidity(Member user, String nickname) {
        memberService.checkLogin(user);
        checkNicknameExists(nickname);
        checkOwner(user, nickname);
    }

    private void checkToggleGlobalBlindRequestValidity(Member user, Integer guestbookId) {
        memberService.checkLogin(user);
        checkGuestbookIdExists(guestbookId);
        checkOwner(user, guestbookId);
    }

    private void checkToggleLocalBlindRequestValidity(Member user, Integer guestbookId) {
        memberService.checkLogin(user);
        checkGuestbookIdExists(guestbookId);
    }

    /*
    기타 메서드
     */
    private void report(Member user, Guestbook target) {
        user.report(new MemberReportGuestbook(user, target));
        target.reportedBy(new MemberReportGuestbook(user, target));
    }

    private void localBlind(Member user, Guestbook target) {
        user.localBlind(new MemberLocalBlindGuestbook(user, target));
        target.localBlindedBy(new MemberLocalBlindGuestbook(user, target));
    }

    private Guestbook getGuestbookById(Integer guestbookId) {
        return guestbookRepository.findById(guestbookId).orElseThrow(() -> {
            throw new NoSuchElementException("해당 id를 가진 방명록이 없습니다.");
        });
    }

    // TODO: MemberService.isNicknameExists 가 같은 역할을 하는 것 같은데 통일해야 함
    private void checkNicknameExists(String nickname){
        if (!memberRepository.existsByNickname(nickname)){
            throw new NoSuchElementException("해당 nickname을 가진 사용자가 없습니다.");
        }
    }

    private void checkGuestbookIdExists(Integer guestbookId){
        if (!guestbookRepository.existsById(guestbookId)){
            throw new NoSuchElementException("해당 id를 가진 방명록이 없습니다.");
        }
    }

    // TODO: 자기자신을 팔로우할 수 없는 CannotFollowException과 합칠 수 있을까?
    private void checkNotOwner(Member user, String nickname) {
        Member owner = memberService.getMemberByNickname(nickname);
        if (user.equals(owner)) {
            throw new MyselfException("자신의 방명록에는 글을 쓸 수 없습니다.");
        }
    }

    private void checkWriter(Member user, Integer guestbookId) {
        Member writer = getGuestbookById(guestbookId).getWriter();
        if (!user.equals(writer)) {
            throw new UnauthorizedException("해당 방명록 글의 작성자가 아닙니다.");
        }
    }

    private void checkNotWriter(Member user, Integer guestbookId) {
        Member writer = getGuestbookById(guestbookId).getWriter();
        if (user.equals(writer)) {
            throw new MyselfException("자신이 작성한 방명록 글은 신고할 수 없습니다.");
        }
    }

    private void checkWriterOrOwner(Member user, Integer guestbookId) {
        Guestbook guestbook = getGuestbookById(guestbookId);
        if (!(user.equals(guestbook.getWriter()) || user.equals(guestbook.getOwner()))) {
            throw new UnauthorizedException("방명록 게시물을 삭제할 권한이 없습니다.");
        }
    }

    private void checkNotOverReported(Integer guestbookId) {
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

    private void checkOwner(Member user, Integer guestbookId) {
        Member owner = getGuestbookById(guestbookId).getOwner();
        if (!user.equals(owner)) {
            throw new UnauthorizedException("방명록 게시물을 숨길 권한이 없습니다.");
        }
    }
}
