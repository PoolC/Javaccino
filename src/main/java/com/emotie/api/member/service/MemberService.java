package com.emotie.api.member.service;

import com.emotie.api.member.domain.Member;
import com.emotie.api.member.dto.MemberCreateRequest;
import com.emotie.api.member.dto.MemberUpdateRequest;

public interface MemberService {
    public void memberCreate(MemberCreateRequest request);

    public void memberUpdate(Member member, MemberUpdateRequest request);

    public Boolean followToggle(Member member, String nickname);

    public void memberWithdrawal(Member member, String nickname);

    public Member getMemberByEmail(String email);

    public Member getMemberIfRegistered(String email, String password);

    public Member getMemberByNickname(String nickname);
}
