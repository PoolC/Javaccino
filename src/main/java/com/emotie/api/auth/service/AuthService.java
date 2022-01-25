package com.emotie.api.auth.service;

import com.emotie.api.auth.dto.PasswordResetRequest;
import com.emotie.api.auth.infra.JwtTokenProvider;
import com.emotie.api.auth.infra.PasswordHashProvider;
import com.emotie.api.common.service.MailService;
import com.emotie.api.member.domain.Member;
import com.emotie.api.member.repository.MemberRepository;
import com.emotie.api.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import net.bytebuddy.utility.RandomString;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final MailService mailService;
    private final PasswordHashProvider passwordHashProvider;

    public String createAccessToken(String loginId, String password) {
        Member member = memberService.getMemberIfRegistered(loginId, password);
        member.loginAndCheckExpelled();
        return jwtTokenProvider.createToken(member);
    }

    @Transactional
    public void sendEmailAuthorizationToken(Optional<String> email) throws Exception {
        String userEmail = email.orElseThrow(NoSuchElementException::new);
        Member member = memberService.getMemberByEmail(userEmail);
        String authorizationToken = checkMemberAuthorizedAndCreateAuthorizationToken(member);
        mailService.sendEmailAuthorizationToken(userEmail, authorizationToken);
    }

    @Transactional
    public void checkAuthorizationTokenRequestAndChangeMemberRole(Optional<String> email, Optional<String> authorizationToken) {
        String userEmail = email.orElseThrow(NoSuchElementException::new);
        Member loginMember = memberService.getMemberByEmail(userEmail);
        loginMember.checkAuthorized();
        String validAuthorizationToken = checkRequestValid(authorizationToken);
        loginMember.checkAuthorizationTokenAndChangeMemberRole(validAuthorizationToken);
        memberRepository.saveAndFlush(loginMember);
    }
    @Transactional
    public void sendEmailPasswordResetToken(Optional<String> email) throws Exception {
        String validEmail = email.get();
        String passwordResetToken = CreatePasswordResetToken(validEmail);
        mailService.sendEmailPasswordResetToken(validEmail, passwordResetToken);
    }

    @Transactional
    public void checkPasswordResetRequestAndUpdatePassword(Optional<String> passwordResetToken, PasswordResetRequest request) {
        Member member = checkPasswordResetRequest(request);
        String validPasswordResetToken = checkRequestValid(passwordResetToken);
        String passwordHash = passwordHashProvider.encodePassword(request.getPassword());
        member.checkPasswordResetTokenAndUpdatePassword(validPasswordResetToken, passwordHash);
    }

    private String checkMemberAuthorizedAndCreateAuthorizationToken(Member loginMember) {
        loginMember.checkAuthorized();
        return createAuthorizationToken(loginMember);
    }

    private String createAuthorizationToken(Member member) {
        String token = createToken();
        member.updateAuthorizationToken(token);

        return token;
    }

    private String CreatePasswordResetToken(String email) {
        Member member = memberService.getMemberByEmail(email);
        String passwordResetToken = createToken();
        member.updatePasswordResetToken(passwordResetToken);
        return passwordResetToken;
    }

    private String createToken() {
        return RandomString.make(40);
    }

    private Member checkPasswordResetRequest(PasswordResetRequest request) {
        Member member = memberService.getMemberByEmail(request.getEmail());
        request.checkRequestValid();
        return member;
    }

    private String checkRequestValid(Optional<String> value) {
        return value.orElseThrow(() -> new IllegalArgumentException("요청값이 틀렸습니다."));
    }
}