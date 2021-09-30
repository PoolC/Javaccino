package com.emotie.api.guestbook.service;

import com.emotie.api.guestbook.domain.Guestbook;
import com.emotie.api.guestbook.domain.MemberLocalBlindGuestbook;
import com.emotie.api.guestbook.domain.MemberReportGuestbook;
import com.emotie.api.guestbook.dto.GuestbookCreateRequest;
import com.emotie.api.guestbook.dto.GuestbookUpdateRequest;
import com.emotie.api.guestbook.repository.GuestbookRepository;
import com.emotie.api.guestbook.repository.MemberLocalBlindGuestbookRepository;
import com.emotie.api.guestbook.repository.MemberReportGuestbookRepository;
import com.emotie.api.member.domain.Member;
import com.emotie.api.member.repository.MemberRepository;
import com.emotie.api.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GuestbookService {
    private final MemberRepository memberRepository;
    private final GuestbookRepository guestbookRepository;
    private final MemberService memberService;
    private final MemberReportGuestbookRepository memberReportGuestbookRepository;
    private final MemberLocalBlindGuestbookRepository memberLocalBlindGuestbookRepository;

    public List<Guestbook> getAllBoards(Member user, String nickname) {
        checkGetAllBoardsRequestValidity(nickname);
        Member owner = memberService.getMemberByNickname(nickname);
        if (user.equals(owner)) {
            return guestbookRepository.findForOwnerByOwner(owner, Guestbook.reportCountThreshold);
        }
        return guestbookRepository.findForUserByOwner(owner, Guestbook.reportCountThreshold);
    }

    public void create(Member user, GuestbookCreateRequest request, String nickname) {
        checkCreateRequestValidity(user, nickname);
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

    public void update(Member user, GuestbookUpdateRequest request, Long guestbookId) {
        checkUpdateRequestValidity(user, guestbookId);
        Guestbook guestbook = getGuestbookById(guestbookId);
        guestbook.update(request);
        guestbookRepository.saveAndFlush(guestbook);
    }

    public Boolean toggleReport(Member user, Long guestbookId) {
        checkToggleReportRequestValidity(user, guestbookId);
        Guestbook target = getGuestbookById(guestbookId);
        Member writer = target.getWriter();
        Optional<MemberReportGuestbook> memberReportGuestbook = memberReportGuestbookRepository.findByMemberAndGuestbook(user, target);
        if (memberReportGuestbook.isPresent()) {
            target.updateReportCount(true);
            guestbookRepository.saveAndFlush(target);
            writer.updateReportCount(true);
            memberRepository.saveAndFlush(writer);
            memberReportGuestbookRepository.delete(memberReportGuestbook.get());
            return false;
        }
        target.updateReportCount(false);
        guestbookRepository.saveAndFlush(target);
        writer.updateReportCount(false);
        memberRepository.saveAndFlush(writer);
        memberReportGuestbookRepository.save(new MemberReportGuestbook(user, target));
        return true;
    }

    public void delete(Member executor, Long guestbookId) {
        checkDeleteRequestValidity(executor, guestbookId);
        Guestbook target = getGuestbookById(guestbookId);
        memberReportGuestbookRepository.deleteAllByGuestbook(target);
        memberLocalBlindGuestbookRepository.deleteAllByGuestbook(target);
        guestbookRepository.deleteById(guestbookId);
    }

    // TODO: cascade 자동으로 할수있는지 확인
    public void clear(Member user, String nickname) {
        checkClearRequestValidity(user, nickname);
        Member owner = memberService.getMemberByNickname(nickname);
        List<Guestbook> guestbookList = guestbookRepository.findByOwner(owner);
        guestbookList.stream().forEach(guestbook -> {
            memberReportGuestbookRepository.deleteAllByGuestbook(guestbook);
            memberLocalBlindGuestbookRepository.deleteAllByGuestbook(guestbook);
            guestbookRepository.delete(guestbook);
        });
    }

    public Boolean toggleGlobalBlind(Member user, Long guestbookId) {
        checkToggleGlobalBlindRequestValidity(user, guestbookId);
        Guestbook target = getGuestbookById(guestbookId);
        target.globalBlind();
        guestbookRepository.saveAndFlush(target);
        return target.getIsGlobalBlinded();
    }

    public Boolean toggleLocalBlind(Member user, Long guestbookId) {
        checkToggleLocalBlindRequestValidity(guestbookId);
        Guestbook target = getGuestbookById(guestbookId);
        Optional<MemberLocalBlindGuestbook> memberLocalBlindGuestbook = memberLocalBlindGuestbookRepository.findByMemberAndGuestbook(user, target);
        if (memberLocalBlindGuestbook.isPresent()) {
            memberLocalBlindGuestbookRepository.delete(memberLocalBlindGuestbook.get());
            return false;
        }
        memberLocalBlindGuestbookRepository.save(new MemberLocalBlindGuestbook(user, target));
        return true;
    }

    /*
    유효성 검사 메서드
    (content null 여부는 Request 검증에서 체크)
     */
    private void checkGetAllBoardsRequestValidity(String nickname) {
        memberService.getMemberByNickname(nickname);
    }

    private void checkCreateRequestValidity(Member user, String nickname) {
        memberService.getMemberByNickname(nickname);
        user.checkNotOwner(nickname);
    }

    private void checkUpdateRequestValidity(Member user, Long guestbookId) {
        Guestbook guestbook = getGuestbookById(guestbookId);
        guestbook.checkWriter(user);
        guestbook.checkNotOverReported();
    }

    private void checkToggleReportRequestValidity(Member user, Long guestbookId) {
        Guestbook guestbook = getGuestbookById(guestbookId);
        guestbook.checkNotWriter(user);
    }

    private void checkDeleteRequestValidity(Member user, Long guestbookId) {
        Guestbook guestbook = getGuestbookById(guestbookId);
        guestbook.checkWriterOrOwner(user);
        guestbook.checkNotOverReported();
    }

    private void checkClearRequestValidity(Member user, String nickname) {
        memberService.getMemberByNickname(nickname);
        user.checkOwner(nickname);
    }

    private void checkToggleGlobalBlindRequestValidity(Member user, Long guestbookId) {
        Guestbook guestbook = getGuestbookById(guestbookId);
        guestbook.checkOwner(user);
    }

    private void checkToggleLocalBlindRequestValidity(Long guestbookId) {
        Guestbook guestbook = getGuestbookById(guestbookId);
    }

    private Guestbook getGuestbookById(Long guestbookId) {
        return guestbookRepository.findById(guestbookId).orElseThrow(() -> {
            throw new NoSuchElementException("해당 id를 가진 방명록이 없습니다.");
        });
    }
}
