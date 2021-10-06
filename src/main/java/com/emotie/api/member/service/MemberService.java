package com.emotie.api.member.service;

import com.emotie.api.auth.exception.UnauthorizedException;
import com.emotie.api.auth.exception.WrongPasswordException;
import com.emotie.api.auth.infra.PasswordHashProvider;
import com.emotie.api.common.exception.DuplicatedException;
import com.emotie.api.emotion.domain.Emotion;
import com.emotie.api.emotion.repository.EmotionRepository;
import com.emotie.api.member.domain.*;
import com.emotie.api.member.dto.MemberCreateRequest;
import com.emotie.api.member.dto.MemberUpdateRequest;
import com.emotie.api.member.dto.PasswordCheckRequest;
import com.emotie.api.member.dto.PasswordUpdateRequest;
import com.emotie.api.member.exception.CannotFollowException;
import com.emotie.api.member.exception.EmotionScoreNotInitializedException;
import com.emotie.api.member.repository.EmotionScoreRepository;
import com.emotie.api.member.repository.FollowRepository;
import com.emotie.api.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final EmotionRepository emotionRepository;
    private final EmotionScoreRepository emotionScoreRepository;
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
        return memberRepository.findByEmail(nickname).orElseThrow(() -> {
            throw new NoSuchElementException("해당 닉네임을 가진 사용자가 없습니다.");
        });
    }

    public Member getMemberByUuid(String uuid) {
        return memberRepository.findById(uuid).orElseThrow(() -> {
            throw new NoSuchElementException("해당 id를 가진 사용자가 없습니다. " + uuid);
        });
    }

    public void create(MemberCreateRequest request) {
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
                .authorizationToken(null)
                .authorizationTokenValidUntil(null)
                .reportCount(0)
                .roles(MemberRoles.getDefaultFor(MemberRole.UNACCEPTED))
                .build();
        memberRepository.save(user);

        initializeEmotionScore(user);
    }

    public void update(Member member, MemberUpdateRequest request) {
        if (!checkNicknameUse(request.getNickname())) {
            throw new DuplicatedException("중복되는 닉네임이 존재합니다.");
        }
        member.updateUserInfo(request);
        memberRepository.saveAndFlush(member);
    }

    public Boolean checkPasswordRight(Member user, PasswordCheckRequest request) {
        return passwordHashProvider.matches(request.getPassword(), user.getPassword());
    }

    public void updatePassword(Member member, PasswordUpdateRequest request) {
        request.checkPasswordMatches();
        String passwordHash = passwordHashProvider.encodePassword(request.getPassword());
        member.updatePassword(passwordHash);
    }

    public Boolean toggleFollowUnfollow(Member fromMember, String toMemberUuid){
        Member toMember = getMemberByUuid(toMemberUuid);
        checkFollowToggleRequestValidity(fromMember,toMember);
        Optional<Follow> follow = followRepository.findFollowByFromMemberAndToMember(fromMember, toMember);
        if(follow.isPresent()){
            followRepository.delete(follow.get());
            return false;
        }
        Follow newFollow = Follow.builder().fromMember(fromMember).toMember(toMember).build();
        followRepository.save(newFollow);
        return true;
    }

//    public Boolean toggleFollowUnfollow(Member user, String followerNickname) {
//        checkFollowToggleRequestValidity(user, followerNickname);
//        Member follower = getMemberByNickname(followerNickname);
//
//        if (user.isFollowing(follower)) {
//            user.unfollow(follower);
//            memberRepository.saveAndFlush(user);
//            memberRepository.saveAndFlush(follower);
//            return false;
//        }
//
//        user.follow(follower);
//        memberRepository.saveAndFlush(user);
//        memberRepository.saveAndFlush(follower);
//        return true;
//    }

    public void delete(Member executor, String uuid) {
        checkDeleteRequestValidity(executor, uuid);
        Member user = getMemberByUuid(uuid);

        // 행위자가 자신과 같으면, 유예 기간이 있고, 아니면 추방
        if (executor.equals(user)) {
            withdrawal(user);
        } else {
            expel(user);
        }
    }

    public void deepenEmotionScore(Member user, String emotion) {
        user.deepenEmotionScore(getEmotionByEmotion(emotion));
        memberRepository.saveAndFlush(user);
    }

    public void reduceEmotionScore(Member user, String emotion) {
        user.reduceEmotionScore(getEmotionByEmotion(emotion));
        memberRepository.saveAndFlush(user);
    }

    public void updateEmotionScore(Member user, String originalEmotion, String updatingEmotion) {
        user.updateEmotionScore(
                getEmotionByEmotion(originalEmotion),
                getEmotionByEmotion(updatingEmotion)
        );
        memberRepository.saveAndFlush(user);
    }

    private void initializeEmotionScore(Member user) {
        List<Emotion> allEmotion = emotionRepository.findAll();

        allEmotion.forEach(
                (emotion) -> initializeEmotionScore(user, emotion)
        );
    }

    private void initializeEmotionScore(Member user, Emotion emotion) {
        EmotionScore emotionScore = EmotionScore.of(
                user.getUUID(),
                emotion,
                0.0
        );
        emotionScoreRepository.save(emotionScore);

        user.initializeEmotionScore(emotion, emotionScore);
        memberRepository.saveAndFlush(user);
    }

    private Emotion getEmotionByEmotion(String emotion) {
        return emotionRepository.findByEmotion(emotion).orElseThrow(
                () -> new NoSuchElementException("해당하는 이름의 감정이 없습니다.")
        );
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

    private void checkDeleteRequestValidity(Member executor, String uuid) {
        // 이 부분에서 유저의 존재성 역시 검증함.
        Member user = getMemberByUuid(uuid);

        // 관리자가 아닐 때는 본인이어야 함.
        if (!executor.getRoles().isAdmin() && !executor.equals(user)) {
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

    private void checkUpdatingEmotionScoreValidity(Member user, Emotion emotion) {
        if (!user.isEmotionScoreInitialized(emotion)) {
            throw new EmotionScoreNotInitializedException("감정 점수가 초기화 되어 있지 않습니다.");
        }
    }

    private void withdrawal(Member user) {
        user.withdraw();
        memberRepository.saveAndFlush(user);
    }

    private void expel(Member user) {
        user.expel();
        memberRepository.saveAndFlush(user);
    }
}
