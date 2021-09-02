package com.emotie.api.guestbook.controller;

import com.emotie.api.guestbook.dto.*;
import com.emotie.api.guestbook.service.GuestbookService;
import com.emotie.api.member.domain.Member;
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

    @GetMapping(value = "/user/{nickname}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GuestbooksResponse> getAllGuestbooks(
            @AuthenticationPrincipal Member user, @PathVariable String nickname
    ) throws Exception {
        List<GuestbookResponse> guestbooks = new ArrayList<>();
        guestbooks = guestbookService.getAllBoards(user, nickname).stream()
                .filter(guestbook -> guestbook.isNotOverReported())
                .map(GuestbookResponse::new)
                .collect(Collectors.toList());
        guestbooks.sort(Comparator.comparing(GuestbookResponse::getCreatedAt));

        GuestbooksResponse response = new GuestbooksResponse(guestbooks);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping(value = "/user/{nickname}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> createGuestbook(
            @AuthenticationPrincipal Member user, @RequestBody @Valid GuestbookCreateRequest request, @PathVariable String nickname
    ) throws Exception {
        guestbookService.create(user, request, nickname);
        return ResponseEntity.ok().build();
    }

    @PutMapping(value = "/{guestbookId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateGuestbook(
            @AuthenticationPrincipal Member user, @RequestBody @Valid GuestbookUpdateRequest request, @PathVariable Integer guestbookId
    ) throws Exception {
        guestbookService.update(user, request, guestbookId);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/report/{guestbookId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GuestbookReportResponse> toggleGuestbookReport(@AuthenticationPrincipal Member user, @PathVariable Integer guestbookId) throws Exception {
        Boolean isReported = guestbookService.toggleReport(user, guestbookId);
        return ResponseEntity.ok(new GuestbookReportResponse(isReported));
    }

    @DeleteMapping(value = "/{guestbookId}")
    public ResponseEntity<Void> deleteGuestbook(@AuthenticationPrincipal Member executor, @PathVariable Integer guestbookId) throws Exception {
        guestbookService.delete(executor, guestbookId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(value = "/user/{nickname}")
    public ResponseEntity<Void> clearGuestbook(@AuthenticationPrincipal Member user, @PathVariable String nickname) throws Exception {
        guestbookService.clear(user, nickname);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "guestbooks/blind/{guestbookId}")
    public ResponseEntity<Void> blindGuestbook(@AuthenticationPrincipal Member user, @PathVariable Integer guestbookId) throws Exception {
        guestbookService.blind(user, guestbookId);
        return ResponseEntity.ok().build();
    }
}
