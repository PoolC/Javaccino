package com.emotie.api.member.service;

import com.emotie.api.auth.exception.UnauthorizedException;
import com.emotie.api.auth.exception.WrongPasswordException;
import com.emotie.api.auth.infra.PasswordHashProvider;
import com.emotie.api.common.exception.DuplicatedException;
import com.emotie.api.emotion.repository.EmotionRepository;
import com.emotie.api.member.domain.Follow;
import com.emotie.api.member.domain.Member;
import com.emotie.api.member.domain.MemberRole;
import com.emotie.api.member.domain.MemberRoles;
import com.emotie.api.member.dto.*;
import com.emotie.api.member.exception.CannotFollowException;
import com.emotie.api.member.repository.FollowRepository;
import com.emotie.api.member.repository.MemberRepository;
import com.emotie.api.profile.dto.FolloweeResponse;
import com.emotie.api.profile.dto.FollowerResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final EmotionRepository emotionRepository;
    private final FollowRepository followRepository;
    private final PasswordHashProvider passwordHashProvider;

    public Member getMemberByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() ->
                        new NoSuchElementException(String.format("해당 이메일(%s)을 가진 회원이 존재하지 않습니다.", email)));
    }

    public Boolean checkNicknameUse(String nickname) {
        return !memberRepository.existsByNickname(nickname);
    }

    public Member getMemberIfRegistered(String email, String password) {
        Member member = getMemberByEmail(email);

        if (!passwordHashProvider.matches(password, member.getPassword()))
            throw new WrongPasswordException("아이디와 비밀번호를 확인해주세요.");

        return member;
    }

    public Member getMemberByNickname(String nickname) {
        return memberRepository.findByNickname(nickname).orElseThrow(() -> {
            throw new NoSuchElementException("해당 닉네임을 가진 사용자가 없습니다.");
        });
    }

    public Member getMemberById(String memberId) {
        return memberRepository.findById(memberId).orElseThrow(() -> {
            throw new NoSuchElementException("해당 id를 가진 사용자가 없습니다. " + memberId);
        });
    }

    @Transactional
    public void create(MemberCreateRequest request, String authorizationToken) {
        checkCreateRequestValidity(request);
        Member user = Member.builder()
                .UUID(UUID.randomUUID().toString())
                .email(request.getEmail())
                .nickname(request.getNickname())
                .passwordHash(passwordHashProvider.encodePassword(request.getPassword()))
                .gender(request.getGender())
                .dateOfBirth(request.getDateOfBirth())
                .introduction("자기 소개를 작성해서 사람들에게 당신을 알려주세요!")
                .passwordResetToken(null)
                .passwordResetTokenValidUntil(null)
                .authorizationToken(authorizationToken)
                .authorizationTokenValidUntil(LocalDateTime.now().plusDays(1L))
                .reportCount(0)
                .roles(MemberRoles.getDefaultFor(MemberRole.UNACCEPTED))
                .build();
        memberRepository.save(user);
    }

    public void update(Member member, MemberUpdateRequest request) {
        if (!member.checkNicknameSame(request.getNickname()) && !checkNicknameUse(request.getNickname())) {
            throw new DuplicatedException("중복되는 닉네임이 존재합니다.");
        }
        member.updateUserInfo(request);
        memberRepository.saveAndFlush(member);
    }

    public Boolean checkPasswordRight(Member user, PasswordCheckRequest request) {
        return passwordHashProvider.matches(request.getPassword(), user.getPassword());
    }

    @Transactional
    public void updatePassword(Member member, PasswordUpdateRequest request) {
        if (!passwordHashProvider.matches(request.getCurrentPassword(), member.getPassword()))
            throw new WrongPasswordException("비밀번호를 확인해주세요.");
        request.checkPasswordMatches();
        String passwordHash = passwordHashProvider.encodePassword(request.getPassword());
        member.updatePassword(passwordHash);
    }

    public Boolean toggleFollowUnfollow(Member fromMember, String toMemberId) {
        Member toMember = getMemberById(toMemberId);
        checkFollowToggleRequestValidity(fromMember, toMember);
        Optional<Follow> follow = followRepository.findFollowByFromMemberAndToMember(fromMember, toMember);
        if (follow.isPresent()) {
            followRepository.delete(follow.get());
            return false;
        }
        Follow newFollow = Follow.builder().fromMember(fromMember).toMember(toMember).build();
        followRepository.save(newFollow);
        return true;
    }

    public boolean isFollowed(Member member, Member profileMember) {
        return followRepository.findFollowByFromMemberAndToMember(member, profileMember).isPresent();
    }

    public List<FolloweeResponse> getFolloweesByMember(Member profileMember) {
        return followRepository.findFollowByFromMember(profileMember).get().stream().map(FolloweeResponse::new).collect(Collectors.toList());
    }

    public List<FollowerResponse> getFollowersByMember(Member profileMember) {
        return followRepository.findFollowByToMember(profileMember).get().stream().map(FollowerResponse::new).collect(Collectors.toList());
    }

    public void delete(Member user, MemberWithdrawalRequest memberWithdrawalRequest) {
        if (!passwordHashProvider.matches(memberWithdrawalRequest.getPassword(), user.getPassword())) {
            throw new WrongPasswordException("비밀번호를 확인해주세요.");
        }
        withdrawal(user, memberWithdrawalRequest);
    }

    private Boolean isNicknameExists(String nickname) {
        return memberRepository.existsByNickname(nickname);
    }

    private Boolean isEmailExists(String email) {
        return memberRepository.existsByEmail(email);
    }

    private void checkCreateRequestValidity(MemberCreateRequest request) {
        checkNicknameUnique(request.getNickname());
        checkEmailUnique(request.getEmail());
        request.checkPasswordMatches();
    }

    private void checkFollowToggleRequestValidity(Member fromMember, Member toMember) {
        checkAuthorized(fromMember);
        checkToMemberIsFollowable(fromMember, toMember);
    }

    private void checkDeleteRequestValidity(Member executor, String memberId) {
        // 이 부분에서 유저의 존재성 역시 검증함.
        Member user = getMemberById(memberId);

        // 관리자가 아닐 때는 본인이어야 함.
        if (!executor.equals(user)) {
            throw new UnauthorizedException("계정을 삭제할 권한이 없습니다.");
        }
    }

    private void checkNicknameUnique(String nickname) {
        if (isNicknameExists(nickname)) {
            throw new DuplicatedException("이미 가입한 닉네임입니다.");
        }
    }

    private void checkEmailUnique(String email) {
        if (isEmailExists(email)) {
            throw new DuplicatedException("이미 가입한 이메일입니다.");
        }
    }

    private void checkAuthorized(Member member) {
        if (!member.getRoles().isAcceptedMember()) throw new UnauthorizedException("인증된 회원만 이용할 수 있는 서비스입니다.");
    }

    private void checkDifferent(Member member1, Member member2) {
        if (member1.equals(member2)) throw new CannotFollowException("자기 자신을 팔로우할 수는 없습니다.");
    }

    private void checkToMemberIsFollowable(Member fromMember, Member toMember) {
        MemberRoles roles = toMember.getRoles();
        checkDifferent(fromMember, toMember);
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

    private void withdrawal(Member user, MemberWithdrawalRequest memberWithdrawalRequest) {
        user.withdraw(memberWithdrawalRequest);
        memberRepository.saveAndFlush(user);
    }
}
