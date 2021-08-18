package com.emotie.api.member.service;

import com.emotie.api.member.domain.Gender;
import com.emotie.api.member.domain.Member;
import com.emotie.api.member.dto.MemberCreateRequest;
import com.emotie.api.member.dto.MemberUpdateRequest;

import java.time.LocalDate;

public interface MemberService {
    public Member getMemberByEmail(String email);

    public Member getMemberIfRegistered(String email, String password);

    public Member getMemberByNickname(String nickname);

    public void create(MemberCreateRequest request);

    public void update(Member member, MemberUpdateRequest request);

    public Boolean toggleFollowUnfollow(Member user, String followerNickname);

    public void delete(Member executor, String nickname);
}
