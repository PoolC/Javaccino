package com.emotie.api.guestbook.controller;

import com.emotie.api.guestbook.dto.GuestbookCreateRequest;
import com.emotie.api.guestbook.dto.GuestbookReportRequest;
import com.emotie.api.guestbook.dto.GuestbookUpdateRequest;
import com.emotie.api.guestbook.dto.GuestbooksResponse;
import com.emotie.api.guestbook.service.GuestbookService;
import com.emotie.api.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@SuppressWarnings({"RedundantThrows", "unused"})
@RestController
@RequestMapping("/guestbooks")
@RequiredArgsConstructor
public class GuestbookController {
    private final GuestbookService guestbookService;

    @GetMapping(value = "/user/{memberId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GuestbooksResponse> getAllGuestbooks(
            @AuthenticationPrincipal Member user, @PathVariable String memberId, @RequestParam Integer page
    ) throws Exception {
        return ResponseEntity.ok(new GuestbooksResponse(guestbookService.getAllBoards(user, memberId, page)));
    }

    @PostMapping(value = "/user/{memberId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> createGuestbook(
            @AuthenticationPrincipal Member user, @RequestBody @Valid GuestbookCreateRequest request, @PathVariable String memberId
    ) throws Exception {
        guestbookService.create(user, request, memberId);
        return ResponseEntity.ok().build();
    }

    @Deprecated
    @PutMapping(value = "/{guestbookId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateGuestbook(
            @AuthenticationPrincipal Member user, @RequestBody @Valid GuestbookUpdateRequest request, @PathVariable Long guestbookId
    ) throws Exception {
        guestbookService.update(user, request, guestbookId);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/report/{guestbookId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> reportGuestbook(@AuthenticationPrincipal Member user, @RequestBody @Valid GuestbookReportRequest request, @PathVariable Long guestbookId) throws Exception {
        guestbookService.report(user, request, guestbookId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(value = "/{guestbookId}")
    public ResponseEntity<Void> deleteGuestbook(@AuthenticationPrincipal Member executor, @PathVariable Long guestbookId) throws Exception {
        guestbookService.delete(executor, guestbookId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(value = "/user/{memberId}")
    public ResponseEntity<Void> clearGuestbook(@AuthenticationPrincipal Member user, @PathVariable String memberId) throws Exception {
        guestbookService.clear(user, memberId);
        return ResponseEntity.ok().build();
    }
}
