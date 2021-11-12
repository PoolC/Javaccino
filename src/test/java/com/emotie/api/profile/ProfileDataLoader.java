package com.emotie.api.profile;

import com.emotie.api.auth.infra.PasswordHashProvider;
import com.emotie.api.diary.domain.Diary;
import com.emotie.api.diary.repository.DiaryRepository;
import com.emotie.api.emotion.domain.Emotion;
import com.emotie.api.emotion.domain.Emotions;
import com.emotie.api.emotion.repository.EmotionRepository;
import com.emotie.api.member.domain.*;
import com.emotie.api.member.repository.FollowRepository;
import com.emotie.api.member.repository.MemberRepository;
import com.emotie.api.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Order(1)
@Component
@Profile("ProfileDataLoader")
@RequiredArgsConstructor
public class ProfileDataLoader implements ApplicationRunner {

    private final MemberRepository memberRepository;
    private final PasswordHashProvider passwordHashProvider;
    private final EmotionRepository emotionRepository;
    private final DiaryRepository diaryRepository;
    private final FollowRepository followRepository;
    private final MemberService memberService;

    public static Member profileMember;
    public static String profileMemberEmail = "profileMember@gmail.com";
    public static String anotherMemberEmail = "anotherMember@gmail.com";
    public static String password = "password123!@";
    public static String profileMemberId = "profileMember";
    public static String profileMemberNickname = "profile-nickname";
    public static String profileMemberIntro = "자기소개입니다.";
    public static String profileMemberIntroUpdated = "자기소개 수정했습니다.";
    private static String anotherMemberId = "anotherMember";
    private static String anotherMemberNickname = "anotherNickname";

    public static Emotion diaryEmotion;

    private static final String EMOTION_BASE_PACKAGE = "com.emotie.api.emotion";

    @Override
    public void run(ApplicationArguments args) throws Exception {
        generateMembers();
        loadEmotion();
        generateDiaries();
    }

    private void generateMembers() {
        profileMember = Member.builder()
                .UUID(profileMemberId)
                .email(profileMemberEmail)
                .nickname(profileMemberNickname)
                .passwordHash(passwordHashProvider.encodePassword(password))
                .gender(Gender.HIDDEN)
                .dateOfBirth(LocalDate.now())
                .introduction(profileMemberIntro)
                .passwordResetToken(null)
                .passwordResetTokenValidUntil(null)
                .authorizationToken(null)
                .authorizationTokenValidUntil(null)
                .reportCount(0)
                .roles(MemberRoles.getDefaultFor(MemberRole.MEMBER))
                .build();
        Member profileMemberEmotion = Member.builder()
                .UUID(anotherMemberId)
                .email(anotherMemberEmail)
                .nickname(anotherMemberNickname)
                .passwordHash(passwordHashProvider.encodePassword(password))
                .gender(Gender.HIDDEN)
                .dateOfBirth(LocalDate.now())
                .introduction("test")
                .passwordResetToken(null)
                .passwordResetTokenValidUntil(null)
                .authorizationToken(null)
                .authorizationTokenValidUntil(null)
                .reportCount(0)
                .roles(MemberRoles.getDefaultFor(MemberRole.MEMBER))
                .build();
        memberRepository.save(profileMember);
        memberRepository.save(profileMemberEmotion);

        createEmotions(profileMember);
        createEmotions(profileMemberEmotion);

        for (int i = 0; i < 4; i++) {
            Member followMember = Member.builder()
                    .UUID("followMember_" + i)
                    .email("follow" + i + "@gamil.com")
                    .nickname("followMember_" + i)
                    .passwordHash(passwordHashProvider.encodePassword(password))
                    .dateOfBirth(LocalDate.now())
                    .gender(Gender.HIDDEN)
                    .introduction("test")
                    .passwordResetToken(null)
                    .passwordResetTokenValidUntil(null)
                    .authorizationToken(null)
                    .authorizationTokenValidUntil(null)
                    .reportCount(0)
                    .roles(MemberRoles.getDefaultFor(MemberRole.MEMBER))
                    .build();
            Member followeeMember = Member.builder()
                    .UUID("followeeMember_" + i)
                    .email("followee" + i + "@gamil.com")
                    .nickname("followeeMember_" + i)
                    .dateOfBirth(LocalDate.now())
                    .passwordHash(passwordHashProvider.encodePassword(password))
                    .dateOfBirth(LocalDate.now())
                    .gender(Gender.HIDDEN)
                    .introduction("test")
                    .passwordResetToken(null)
                    .passwordResetTokenValidUntil(null)
                    .authorizationToken(null)
                    .authorizationTokenValidUntil(null)
                    .reportCount(0)
                    .roles(MemberRoles.getDefaultFor(MemberRole.MEMBER))
                    .build();
            memberRepository.save(followMember);
            memberRepository.save(followeeMember);
            followRepository.save(new Follow(profileMember, followeeMember));
            followRepository.save(new Follow(followMember, profileMember));

            createEmotions(followMember);
            createEmotions(followeeMember);
        }
    }

    private void loadEmotion() {
        diaryEmotion = emotionRepository.findByMemberAndName(profileMember, "happy").get();
    }

    private void generateDiaries() {
        for (int i = 0; i < 4; i++) {
            diaryRepository.save(
                    Diary.builder()
                            .writer(profileMember)
                            .emotion(diaryEmotion)
                            .content("test")
                            .isOpened(true)
                            .build()
            );
            deepenDiaryEmotion();
        }
        Emotion flutter = emotionRepository.findByMemberAndName(profileMember, "flutter").get();
        for (int i = 0; i < 3; i++) {
            diaryRepository.save(
                    Diary.builder()
                            .writer(profileMember)
                            .emotion(flutter)
                            .content("test")
                            .isOpened(true)
                            .build());
        }
        Emotion jealous = emotionRepository.findByMemberAndName(profileMember, "jealous").get();
        diaryRepository.save(
                Diary.builder()
                        .writer(profileMember)
                        .emotion(jealous)
                        .content("test")
                        .isOpened(true)
                        .build());
    }

    private void deepenDiaryEmotion() {
        Emotions emotions = new Emotions(profileMember, emotionRepository.findAllByMember(profileMember));
        emotions.deepenCurrentEmotionScore(diaryEmotion.getName());
        emotionRepository.saveAllAndFlush(emotions.allMemberEmotions());
    }

    private void createEmotions(Member member) {
        List<Emotion> emotions = new Reflections(EMOTION_BASE_PACKAGE, new SubTypesScanner())
                .getSubTypesOf(Emotion.class).stream()
                .map(concreteEmotionClass -> {
                    try {
                        return concreteEmotionClass.getDeclaredConstructor(Member.class).newInstance(member);
                    } catch (Exception e) {
                        throw new RuntimeException("Couldn't create concrete Emotion class\n" + e.getMessage());
                    }
                })
                .collect(Collectors.toList());
        emotionRepository.saveAllAndFlush(emotions);
    }
}

