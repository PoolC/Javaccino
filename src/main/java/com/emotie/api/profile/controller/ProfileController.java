package com.emotie.api.profile.controller;

import com.emotie.api.member.domain.Member;
import com.emotie.api.profile.dto.ProfileResponse;
import com.emotie.api.profile.dto.ProfileUpdateRequest;
import com.emotie.api.profile.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/profiles")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/{memberId}")
    public ResponseEntity<ProfileResponse> getProfile
            (@AuthenticationPrincipal Member user, @PathVariable String memberId){
        return ResponseEntity.ok(profileService.getProfile(user,memberId));
    }

    @PutMapping()
    public ResponseEntity<Void> updateProfile(@AuthenticationPrincipal Member user,  @RequestBody @Valid ProfileUpdateRequest profileUpdateRequest){
        profileService.updateProfile(user, profileUpdateRequest.getIntroduction());
        return ResponseEntity.ok().build();
    }
}
