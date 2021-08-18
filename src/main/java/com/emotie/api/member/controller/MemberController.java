package com.emotie.api.member.controller;

import com.emotie.api.member.domain.Member;
import com.emotie.api.member.dto.MemberCreateRequest;
import com.emotie.api.member.dto.MemberFollowResponse;
import com.emotie.api.member.dto.MemberUpdateRequest;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public interface MemberController {
    ResponseEntity<Void> register(MemberCreateRequest request) throws Exception;

    ResponseEntity<Void> updateMemberInformation(
            Member user, MemberUpdateRequest request
    ) throws Exception;

    ResponseEntity<MemberFollowResponse> toggleMemberFollow(Member user, String nickname) throws Exception;

    ResponseEntity<Void> deleteMember(Member executor, String nickname) throws Exception;
}
