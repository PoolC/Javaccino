package com.emotie.api.search.service;

import com.emotie.api.member.domain.Member;
import com.emotie.api.profile.dto.ProfileResponse;
import com.emotie.api.profile.dto.ProfilesResponse;
import com.emotie.api.profile.service.ProfileService;
import com.emotie.api.search.repository.SearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchService {
    private final SearchRepository searchRepository;
    private final ProfileService profileService;

    private final Integer PAGE_SIZE = 10;

    public ProfilesResponse searchProfile(Member user, String keyword, Integer page) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE);
        List<Member> searchedMembers = searchRepository.findByNicknameContainingIgnoreCase(keyword,pageable);
        List<ProfileResponse> searchResult =
                searchedMembers.stream().filter(
                                member -> member.getRoles().isMember()
                        )
                        .map(
                                member -> profileService.getProfile(user, member.getUUID())
                        ).collect(Collectors.toList());
        return ProfilesResponse.builder().profiles(searchResult).build();

    }
}
