package com.emotie.api.guestbook.service;

import com.emotie.api.guestbook.domain.Guestbook;
import com.emotie.api.guestbook.domain.MemberReportGuestbook;
import com.emotie.api.guestbook.dto.GuestbookCreateRequest;
import com.emotie.api.guestbook.dto.GuestbookReportRequest;
import com.emotie.api.guestbook.dto.GuestbookResponse;
import com.emotie.api.guestbook.dto.GuestbookUpdateRequest;
import com.emotie.api.guestbook.repository.GuestbookRepository;
import com.emotie.api.guestbook.repository.MemberReportGuestbookRepository;
import com.emotie.api.member.domain.Member;
import com.emotie.api.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GuestbookService {
    private final GuestbookRepository guestbookRepository;
    private final MemberService memberService;
    private final MemberReportGuestbookRepository memberReportGuestbookRepository;

    public static final Integer PAGE_SIZE = 10;

    public List<GuestbookResponse> getAllBoards(Member user, String memberId, Integer page) {
        checkGetAllBoardsRequestValidity(memberId);
        Member owner = memberService.getMemberById(memberId);
        Pageable pageable = PageRequest.of(page, PAGE_SIZE, Sort.by("createdAt").descending());
        List<Guestbook> guestbooks = guestbookRepository.findByOwner(user, owner, Guestbook.reportCountThreshold, pageable);
        return guestbooks.stream()
                .map(GuestbookResponse::of)
                .collect(Collectors.toList());
    }

    public void create(Member user, GuestbookCreateRequest request, String memberId) {
        checkCreateRequestValidity(user, memberId);
        Member owner = memberService.getMemberById(memberId);
        guestbookRepository.save(
                Guestbook.builder()
                        .owner(owner)
                        .writer(user)
                        .content(request.getContent())
                        .reportCount(0)
                        .isOwnerReported(false)
                        .build()
        );
    }

    @Deprecated
    public void update(Member user, GuestbookUpdateRequest request, Long guestbookId) {
        checkUpdateRequestValidity(user, guestbookId);
        Guestbook guestbook = getGuestbookById(guestbookId);
        guestbook.update(request);
        guestbookRepository.saveAndFlush(guestbook);
    }

    public void report(Member user, GuestbookReportRequest request, Long guestbookId) {
        checkReportRequestValidity(user, guestbookId);
        Guestbook target = getGuestbookById(guestbookId);
        if (user.equals(target.getOwner())) {
            target.ownerReport();
        }
        target.addReportCount();
        guestbookRepository.saveAndFlush(target);
        memberReportGuestbookRepository.save(new MemberReportGuestbook(user, target, request.getReason()));
    }

    public void delete(Member executor, Long guestbookId) {
        checkDeleteRequestValidity(executor, guestbookId);
        Guestbook target = getGuestbookById(guestbookId);
        memberReportGuestbookRepository.deleteAllByGuestbook(target);
        guestbookRepository.deleteById(guestbookId);
    }

    // TODO: cascade 자동으로 할수있는지 확인
    public void clear(Member user, String memberId) {
        checkClearRequestValidity(user, memberId);
        Member owner = memberService.getMemberById(memberId);
        List<Guestbook> guestbookList = guestbookRepository.findByOwner(owner);
        guestbookList.stream().forEach(guestbook -> {
            memberReportGuestbookRepository.deleteAllByGuestbook(guestbook);
            guestbookRepository.delete(guestbook);
        });
    }

    /*
    유효성 검사 메서드
    (content null 여부는 Request 검증에서 체크)
     */
    private void checkGetAllBoardsRequestValidity(String memberId) {
        memberService.getMemberById(memberId);
    }

    private void checkCreateRequestValidity(Member user, String memberId) {
        memberService.getMemberById(memberId);
        user.checkNotOwner(memberId);
    }

    private void checkUpdateRequestValidity(Member user, Long guestbookId) {
        Guestbook guestbook = getGuestbookById(guestbookId);
        guestbook.checkWriter(user);
        guestbook.checkNotOverReported();
    }

    private void checkReportRequestValidity(Member user, Long guestbookId) {
        Guestbook guestbook = getGuestbookById(guestbookId);
        guestbook.checkNotWriter(user);
    }

    private void checkDeleteRequestValidity(Member user, Long guestbookId) {
        Guestbook guestbook = getGuestbookById(guestbookId);
        guestbook.checkWriter(user);
        guestbook.checkNotOverReported();
    }

    private void checkClearRequestValidity(Member user, String memberId) {
        memberService.getMemberById(memberId);
        user.checkOwner(memberId);
    }

    private Guestbook getGuestbookById(Long guestbookId) {
        return guestbookRepository.findById(guestbookId).orElseThrow(() -> {
            throw new NoSuchElementException("해당 id를 가진 방명록이 없습니다.");
        });
    }
}
