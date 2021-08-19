package com.emotie.api.member.controller;

import com.emotie.api.member.domain.Member;
import com.emotie.api.member.dto.MemberCreateRequest;
import com.emotie.api.member.dto.MemberFollowResponse;
import com.emotie.api.member.dto.MemberUpdateRequest;
import com.emotie.api.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @PostMapping
    public ResponseEntity<Void> memberCreate(@RequestBody MemberCreateRequest request){
        memberService.memberCreate(request);
        return ResponseEntity.ok().build();
    }

    @PutMapping
    public ResponseEntity<Void> memberUpdate(@AuthenticationPrincipal Member member, @RequestBody MemberUpdateRequest request){
        memberService.memberUpdate(member, request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/follow/{nickname}")
    public ResponseEntity<MemberFollowResponse> followToggle(@AuthenticationPrincipal Member member, @PathVariable String nickname){
        Boolean isFollowing = memberService.followToggle(member, nickname);
        return ResponseEntity.ok(new MemberFollowResponse(isFollowing));
    }

    @DeleteMapping("/{nickname}")
    public ResponseEntity<Void> memberWithdrawal(@AuthenticationPrincipal Member member, @PathVariable String nickname){
        memberService.memberWithdrawal(member, nickname);
        return ResponseEntity.ok().build();
    }

}
