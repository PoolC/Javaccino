package com.emotie.api.member.controller;

import com.emotie.api.member.domain.Member;
import com.emotie.api.member.dto.*;
import com.emotie.api.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@SuppressWarnings({"RedundantThrows", "unused"})
@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {
    // TODO: 2021-08-20 컨트롤러에서 유효성 검사 
    private final MemberService memberService;

    @PostMapping
    public ResponseEntity<Void> register(@RequestBody @Valid MemberCreateRequest request) throws Exception {
        memberService.create(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    public ResponseEntity<MemberResponse> getMyInformation(@AuthenticationPrincipal Member user) {
        MemberResponse response = MemberResponse.of(user);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/nickname")
    public ResponseEntity<Map<String, Boolean>> checkNicknameDuplicate(@RequestBody @Valid NicknameCheckRequest request) {
        Boolean checkNicknameDuplicateFlag = memberService.checkNicknameUse(request.getNickname());
        return ResponseEntity.ok().body(Map.of("checkNickname", checkNicknameDuplicateFlag));
    }

    @PutMapping
    public ResponseEntity<Void> updateMemberInformation(
            @AuthenticationPrincipal Member user, @RequestBody @Valid MemberUpdateRequest request
    ) throws Exception {
        memberService.update(user, request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/password")
    public ResponseEntity<Map<String, Boolean>> checkPasswordRight(@AuthenticationPrincipal Member user,
                                                                   @RequestBody @Valid PasswordCheckRequest request) {
        Boolean checkPasswordRightFlag = memberService.checkPasswordRight(user, request);
        return ResponseEntity.ok().body(Map.of("checkPassword", checkPasswordRightFlag));
    }

    @PutMapping("/password")
    public ResponseEntity<Void> updateMemberPassword(
            @AuthenticationPrincipal Member user, @RequestBody @Valid PasswordUpdateRequest request
    ) throws Exception {
        memberService.updatePassword(user, request);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping(value = "/follow/{nickname}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MemberFollowResponse> toggleMemberFollow(
            @AuthenticationPrincipal Member user, @PathVariable String nickname
    ) throws Exception {
        Boolean isFollowing = memberService.toggleFollowUnfollow(user, nickname);
        return ResponseEntity.ok(new MemberFollowResponse(isFollowing));
    }

    @DeleteMapping("/{nickname}")
    public ResponseEntity<Void> deleteMember(@AuthenticationPrincipal Member executor, @PathVariable String nickname) throws Exception {
        memberService.delete(executor, nickname);
        return ResponseEntity.ok().build();
    }
}
