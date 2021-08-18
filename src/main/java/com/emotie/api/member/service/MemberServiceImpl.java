package com.emotie.api.member.service;

import com.emotie.api.auth.exception.UnauthenticatedException;
import com.emotie.api.auth.exception.WrongPasswordException;
import com.emotie.api.auth.infra.PasswordHashProvider;
import com.emotie.api.member.domain.Gender;
import com.emotie.api.member.domain.Member;
import com.emotie.api.member.domain.MemberRole;
import com.emotie.api.member.domain.MemberRoles;
import com.emotie.api.member.dto.MemberCreateRequest;
import com.emotie.api.member.dto.MemberUpdateRequest;
import com.emotie.api.member.exception.CannotFollowException;
import com.emotie.api.member.exception.DuplicatedMemberException;
import com.emotie.api.member.exception.InvalidNicknameException;
import com.emotie.api.member.repository.FolloweesRepository;
import com.emotie.api.member.repository.FollowersRepository;
import com.emotie.api.member.repository.MemberRepository;
import com.sun.xml.bind.v2.TODO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.lang.management.OperatingSystemMXBean;
import java.time.LocalDate;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;
    private final FolloweesRepository followeesRepository;
    private final FollowersRepository followersRepository;

    private final PasswordHashProvider passwordHashProvider;

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
        return memberRepository.findByEmail(nickname).orElseThrow(() -> {
            throw new NoSuchElementException("해당 닉네임을 가진 사용자가 없습니다.");
        });
    }

    @Override
    public void create(MemberCreateRequest request) {
        checkCreateRequestValidity(request);
        memberRepository.save(
                Member.builder()
                        .UUID(UUID.randomUUID().toString())
                        .passwordHash(passwordHashProvider.encodePassword(request.getPassword()))
                        .nickname(request.getNickname())
                        .gender(request.getGender())
                        .dateOfBirth(request.getDateOfBirth())
                        .email(request.getEmail())
                        .passwordResetToken(null)
                        .passwordResetTokenValidUntil(null)
                        .authorizationToken(null)
                        .authorizationTokenValidUntil(null)
                        .reportCount(0)
                        .roles(MemberRoles.getDefaultFor(MemberRole.UNACCEPTED))
                        .build()
        );
    }

    @Override
    public void update(Member member, MemberUpdateRequest request) {
        checkUpdateRequestValidity(member, request);
        String passwordHash = passwordHashProvider.encodePassword(request.getPassword());
        member.updateUserInfo(request, passwordHash);
        memberRepository.saveAndFlush(member);
    }

    @Override
    public Boolean toggleFollowUnfollow(Member user, String followerNickname) {
        checkFollowToggleRequestValidity(user, followerNickname);
        Member follower = getMemberByNickname(followerNickname);

        if (user.isFollowing(follower)) {
            follow(user, follower);
            return false;
        } else {
            unfollow(user, follower);
            return true;
        }
    }

    @Override
    public void delete(Member executor, String nickname) {
        checkDeleteRequestValidity(executor, nickname);
        Member user = getMemberByNickname(nickname);

        // 행위자가 자신과 같으면, 유예 기간이 있고, 아니면 추방
        if (executor.equals(user)) {
            withdrawal(user);
        } else {
            expel(user);
        }
    }

    private Boolean isNicknameValid(String nickname) {
        // TODO: 2021-08-18 기준? 
        return true;
    }

    private Boolean isNicknameExists(String nickname) {
        return memberRepository.existsByNickname(nickname);
    }

    private Boolean isEmailExists(String email) {
        return memberRepository.existsByEmail(email);
    }

    private void checkCreateRequestValidity(MemberCreateRequest request) {
        checkNicknameValidity(request.getNickname());
        checkEmailValidity(request.getEmail());
        request.checkPasswordMatches();
    }

    private void checkUpdateRequestValidity(Member member, MemberUpdateRequest request) {
        checkLogin(member);
        request.checkPasswordMatches();
    }

    private void checkFollowToggleRequestValidity(Member member, String nickname) {
        checkLogin(member);
        checkNicknameIsFollowable(nickname);

    }

    private void checkDeleteRequestValidity(Member executor, String nickname) {
        checkLogin(executor);

        // 이 부분에서 유저의 존재성 역시 검증함.
        Member user = getMemberByNickname(nickname);

        // 관리자가 아닐 때는 본인이어야 함.
        if (!executor.getRoles().isAdmin() && !executor.equals(user)) {
            throw new UnauthenticatedException("계정을 삭제할 권한이 없습니다.");
        }
    }

    private void checkNicknameValidity(String nickname) {
        if (!isNicknameValid(nickname)) {
            throw new InvalidNicknameException("잘못 된 닉네임 형식입니다.");
        }

        if (isNicknameExists(nickname)) {
            throw new DuplicatedMemberException("이미 가입한 닉네임입니다.");
        }
    }

    private void checkEmailValidity(String email) {
        if (isEmailExists(email)) {
            throw new DuplicatedMemberException("이미 가입한 이메일입니다.");
        }
    }

    private void checkLogin(Member member) {
        Optional.ofNullable(member)
                .orElseThrow(() -> {
                    throw new NoSuchElementException("로그인하지 않았습니다.");
                });
    }

    private void checkNicknameIsFollowable(String nickname){
        Member follower = getMemberByNickname(nickname);
        MemberRoles roles = follower.getRoles();
        if (!roles.isAcceptedMember()) {
            throw new CannotFollowException("해당 사용자는 아직 인증되지 않아, 팔로우 신청할 수 없습니다.");
        }

        if (roles.isExpelled()) {
            throw new CannotFollowException("해당 사용자는 강제로 탈퇴당한 회원이라, 팔로우 신청할 수 없습니다.");
        }

        if (roles.hasRole(MemberRole.WITHDRAWAL)) {
            throw new CannotFollowException("해당 사용자는 탈퇴한 회원이라, 팔로우 신청할 수 없습니다.");
        }
    }

    private void follow(Member user, Member follower) {
        // TODO: 2021-08-18 ???? 
    }

    private void unfollow(Member user, Member follower) {
        // TODO: 2021-08-18 ????
    }

    private void withdrawal(Member user) {
        // TODO: 2021-08-18
    }

    private void expel(Member user) {

    }
}
