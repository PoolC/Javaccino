package com.emotie.api.profile;

import com.emotie.api.auth.infra.PasswordHashProvider;
import com.emotie.api.diary.repository.DiaryRepository;
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
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Component
@Profile("ProfileDataLoader")
@RequiredArgsConstructor
public class ProfileDataLoader implements ApplicationRunner {

    private final MemberRepository memberRepository;
    private final PasswordHashProvider passwordHashProvider;
    private final EmotionRepository emotionRepository;
    private final DiaryRepository diaryRepository;

    private static String profileMemberId = "profileMember";
    private static String profileMemberNickname = "nickname";
    private static String profileMemberIntro = "자기소개입니다.";

    @Transactional
    @Override
    public void run(ApplicationArguments args) throws Exception {

    }

    private void generateMembers(){

        Member profileMember = Member.builder()
                .UUID(profileMemberId)
                .email("authorizationToken@gmail.com")
                .nickname(profileMemberNickname)
                .passwordHash(passwordHashProvider.encodePassword("password123!@"))
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


    }

}

