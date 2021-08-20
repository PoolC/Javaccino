package com.emotie.api.member.service;

import com.emotie.api.auth.exception.WrongPasswordException;
import com.emotie.api.auth.infra.PasswordHashProvider;
import com.emotie.api.member.domain.Gender;
import com.emotie.api.member.domain.Member;
import com.emotie.api.member.domain.MemberRole;
import com.emotie.api.member.domain.MemberRoles;
import com.emotie.api.member.dto.MemberCreateRequest;
import com.emotie.api.member.dto.MemberUpdateRequest;
import com.emotie.api.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService{
    private final MemberRepository memberRepository;

    private final PasswordHashProvider passwordHashProvider;

    @Override
    public void memberCreate(MemberCreateRequest request){
        checkCreateRequestValid(request);
        memberRepository.save(
                Member.builder()
                        .UUID(UUID.randomUUID().toString())
                        .email(request.getEmail())
                        .nickname(request.getNickname())
                        .passwordHash(passwordHashProvider.encodePassword(request.getPassword()))
                        .gender(Gender.valueOf(request.getGender()))
                        .dateOfBirth(request.getDateOfBirth())
                        .introduction("Initial introduction")
                        .passwordResetToken(null)
                        .passwordResetTokenValidUntil(null)
                        .authorizationToken(null)
                        .authorizationTokenValidUntil(null)
                        .reportCount(0)
                        .roles(MemberRoles.getDefaultFor(MemberRole.MEMBER))
                        .build()
        );
    }

    @Override
    public void memberUpdate(Member member, MemberUpdateRequest request){
        checkUpdateRequestValid(member, request);
        String encodedPassword = passwordHashProvider.encodePassword(request.getPassword());
        member.updateInfo(request, encodedPassword);
        memberRepository.saveAndFlush(member);
    }

    @Override
    public Boolean followToggle(Member follower, String followeeNickname){
        checkFollowRequestValid(follower, followeeNickname);
        Member followee = getMemberByNickname(followeeNickname);
        if (!followee.isFollowing(followeeNickname)) {
            followee.follow(followeeNickname);
            return true;
        }
        else {
            followee.unfollow(followeeNickname);
            return false;
        }
        // TODO: repository 처리 부분 구현
    }

    @Override
    public void memberWithdrawal(Member executor, String nickname){
        checkWithdrawalRequestValid(executor, nickname);
        Member targetMember = getMemberByNickname(nickname);
        if (executor.equals(targetMember)) {
            targetMember.withdrawal();
        }
        else {
            targetMember.expel();
        }
        memberRepository.saveAndFlush(executor);
    }

    @Override
    public Member getMemberByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() ->
                        new NoSuchElementException(String.format("해당 이메일(%s)을 가진 회원이 존재하지 않습니다.", email)));
    }

    @Override
    public Member getMemberIfRegistered(String email, String password) {
        Member member = getMemberByEmail(email);

        if (!passwordHashProvider.matches(password, member.getPassword()))
            throw new WrongPasswordException("아이디와 비밀번호를 확인해주세요.");

        return member;
    }

    @Override
    public Member getMemberByNickname(String nickname) {
        return memberRepository.findByNickname(nickname)
                .orElseThrow(() ->
                        new NoSuchElementException(String.format("해당 닉네임(%s)을 가진 회원이 존재하지 않습니다.", nickname)));
    }
}
