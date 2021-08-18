package com.emotie.api.member.controller;

import com.emotie.api.member.domain.Member;
import com.emotie.api.member.dto.MemberCreateRequest;
import com.emotie.api.member.dto.MemberFollowResponse;
import com.emotie.api.member.dto.MemberUpdateRequest;
import com.emotie.api.member.exception.DuplicatedMemberException;
import com.emotie.api.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberControllerImpl implements MemberController{
    private final MemberService memberService;

    @Override
    @PostMapping
    public ResponseEntity<Void> register(@RequestBody MemberCreateRequest request) throws Exception {
        memberService.create(request);
        return ResponseEntity.ok().build();
    }

    @Override
    @PutMapping
    public ResponseEntity<Void> updateMemberInformation(
            @AuthenticationPrincipal Member user, @RequestBody MemberUpdateRequest request
    ) throws Exception {
        memberService.update(user, request);
        return ResponseEntity.ok().build();
    }

    @Override
    @PostMapping(value = "/follow/{nickname}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MemberFollowResponse> toggleMemberFollow(
            @AuthenticationPrincipal Member user, @PathVariable String nickname
    ) throws Exception {
        Boolean isFollowing = memberService.toggleFollowUnfollow(user, nickname);
        return ResponseEntity.ok(new MemberFollowResponse(isFollowing));
    }

    @Override
    @DeleteMapping("/{nickname}")
    public ResponseEntity<Void> deleteMember(@AuthenticationPrincipal Member executor, @PathVariable String nickname) throws Exception {
        memberService.delete(executor, nickname);
        return null;
    }
}
