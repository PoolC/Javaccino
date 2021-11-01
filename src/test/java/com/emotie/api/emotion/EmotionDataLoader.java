package com.emotie.api.emotion;

import com.emotie.api.auth.infra.PasswordHashProvider;
import com.emotie.api.diary.repository.DiaryRepository;
import com.emotie.api.emotion.domain.HappyEmotion;
import com.emotie.api.emotion.repository.EmotionRepository;
import com.emotie.api.member.domain.Gender;
import com.emotie.api.member.domain.Member;
import com.emotie.api.member.domain.MemberRole;
import com.emotie.api.member.domain.MemberRoles;
import com.emotie.api.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Order(1)
@Component
@Profile("EmotionDataLoader")
@RequiredArgsConstructor
public class EmotionDataLoader implements ApplicationRunner {
    public final static String MEMBER_EMAIL = "member1@gmail.com";
    public final static String MEMBER_PASSWORD = "password123!";
    public final static String MEMBER_INTRODUCTION = "안녕하세요";

    public static Member member;

    private final MemberRepository memberRepository;
    private final PasswordHashProvider passwordHashProvider;
    private final EmotionRepository emotionRepository;
    private final DiaryRepository diaryRepository;

    @Transactional
    @Override
    public void run(ApplicationArguments args) throws Exception {
        generate();
    }

    private void generate() {
        member = member.builder()
                .UUID(UUID.randomUUID().toString())
                .email(MEMBER_EMAIL)
                .nickname(MEMBER_EMAIL)
                .passwordHash(passwordHashProvider.encodePassword(MEMBER_PASSWORD))
                .gender(Gender.HIDDEN)
                .dateOfBirth(LocalDate.now())
                .introduction(MEMBER_INTRODUCTION)
                .passwordResetToken(null)
                .passwordResetTokenValidUntil(LocalDateTime.now().minusDays(1))
                .authorizationToken(null)
                .authorizationTokenValidUntil(null)
                .reportCount(0)
                .roles(MemberRoles.getDefaultFor(MemberRole.ADMIN))
                .build();
        memberRepository.saveAndFlush(member);
        emotionRepository.save(new HappyEmotion(member, 0.0));
    }
}
