package com.emotie.api.search.controller;

import com.emotie.api.member.domain.Member;
import com.emotie.api.profile.dto.ProfileCardsResponse;
import com.emotie.api.profile.dto.ProfilesResponse;
import com.emotie.api.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @GetMapping("/{keyword}")
    public ResponseEntity<ProfileCardsResponse> searchProfile
            (@AuthenticationPrincipal Member user, @PathVariable String keyword , @RequestParam Integer page){
        return ResponseEntity.ok(searchService.searchProfile(user, keyword, page));
    }

}
