package com.emotie.api.guestbook.controller;

import com.emotie.api.guestbook.dto.GuestbookReportResponse;
import com.emotie.api.guestbook.dto.GuestbookResponse;
import com.emotie.api.guestbook.dto.GuestbooksResponse;
import com.emotie.api.member.domain.Member;
import com.emotie.api.member.dto.MemberUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings({"RedundantThrows", "unused"})
@RestController
@RequestMapping("/guestbooks")
@RequiredArgsConstructor
public class GuestbookController {
    private final GuestbookService guestbookService;

    @GetMapping(value = "/{nickname}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GuestbooksResponse> getAllGuestbooks(
            @AuthenticationPrincipal Member user, @PathVariable String nickname
    ) throws Exception {
        List<GuestbookResponse> guestbooks = new ArrayList<>();
        guestbooks = guestbookService.getAllBoards(nickname).stream()
                .filter(guestbook -> guestbook.isNotBlinded())
                .map(GuestbookResponse::new)
                .collect(Collectors.toList());
        guestbooks.sort(Comparator.comparing(GuestbookResponse::getCreatedAt));

        GuestbooksResponse response = new GuestbooksResponse(guestbooks);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping(value = "/{nickname}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> createGuestbook(
            @AuthenticationPrincipal Member user, @RequestBody @Valid MemberUpdateRequest request, @PathVariable String nickname
    ) throws Exception {
        guestbookService.create(user, request, nickname);
        return ResponseEntity.ok().build();
    }

    @PutMapping(value = "/{guestbookId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateGuestbook(
            @AuthenticationPrincipal Member user, @RequestBody @Valid MemberUpdateRequest request, @PathVariable Integer guestbookId
    ) throws Exception {
        guestbookService.update(user, request, guestbookId);
        return ResponseEntity.ok().build();
    }

    @PutMapping(value = "/report/{nickname}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GuestbookReportResponse> toggleGuestbookReport(@AuthenticationPrincipal Member user, @PathVariable String nickname) throws Exception {
        Boolean isReported = guestbookService.toggleReport(user, nickname);
        return ResponseEntity.ok(new GuestbookReportResponse(isReported));
    }

    @DeleteMapping(value = "/{guestbookId}")
    public ResponseEntity<Void> deleteGuestbook(@AuthenticationPrincipal Member executor, @PathVariable Integer guestbookId) throws Exception {
        guestbookService.delete(executor, guestbookId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(value = "/clear/{nickname}")
    public ResponseEntity<Void> clearGuestbook(@AuthenticationPrincipal Member user, @PathVariable String nickname) throws Exception {
        guestbookService.clear(user, nickname);
        return ResponseEntity.ok().build();
    }
}
