package com.emotie.api.member.service;

import com.emotie.api.member.domain.Member;

public interface MemberService {
    public Member getMemberByEmail(String email);

    public Member getMemberIfRegistered(String email, String password);
}
