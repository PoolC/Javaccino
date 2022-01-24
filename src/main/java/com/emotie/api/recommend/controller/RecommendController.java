package com.emotie.api.recommend.controller;

import com.emotie.api.member.domain.Member;
import com.emotie.api.profile.dto.ProfileCardsResponse;
import com.emotie.api.profile.dto.ProfilesResponse;
import com.emotie.api.recommend.service.RecommendService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/recommend")
@RequiredArgsConstructor
public class RecommendController {
    public final RecommendService recommendService;

    @GetMapping()
    public ResponseEntity<ProfileCardsResponse> getRecommendations(@AuthenticationPrincipal Member user) {
        return ResponseEntity.ok(recommendService.recommendProfilesToUser(user));
    }
}
