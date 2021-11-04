package com.emotie.api.profile.controller;

import com.emotie.api.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/profiles")
@RequiredArgsConstructor
public class ProfileController {


    @GetMapping("/{uuid}")
    public ResponseEntity<Void> getProfile(@AuthenticationPrincipal Member user, @PathVariable Integer uuid){

    return null;
    }
}
