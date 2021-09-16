package com.emotie.api.guestbook;

import com.emotie.api.auth.infra.PasswordHashProvider;
import com.emotie.api.common.domain.Postings;
import com.emotie.api.guestbook.domain.Guestbook;
import com.emotie.api.guestbook.repository.GuestbookRepository;
import com.emotie.api.member.domain.Gender;
import com.emotie.api.member.domain.Member;
import com.emotie.api.member.domain.MemberRole;
import com.emotie.api.member.domain.MemberRoles;
import com.emotie.api.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.UUID;

@Component
@Profile("guestbookDataLoader")
@RequiredArgsConstructor
public class GuestbookDataLoader implements CommandLineRunner {
    private final GuestbookRepository guestbookRepository;
    private final PasswordHashProvider passwordHashProvider;
    private final MemberRepository memberRepository;

    public static String
            writerEmail = "writer@naver.com",
            ownerEmail = "owner@naver.com",
            writerNickname = "Writer Kim",
            ownerNickname = "Owner Park",
            notExistNickname = "Not exist nickname",
            guestbookPassword = "qhdks1!",
            introduction = "We are Guestbook test users",
            createContent = "구독하고 갑ㄴ디ㅏ",
            changedContent = "구독하고 갑니다";

    public static Integer existId = 1, overReportedId = 2, notExistId = -1;

    @Override
    public void run(String... args) throws Exception {
        // 방명록 작성자
        memberRepository.save(
                Member.builder()
                        .UUID(UUID.randomUUID().toString())
                        .email(writerEmail)
                        .nickname(writerNickname)
                        .passwordHash(passwordHashProvider.encodePassword(guestbookPassword))
                        .gender(Gender.HIDDEN)
                        .dateOfBirth(LocalDate.now())
                        .introduction(introduction)
                        .passwordResetToken(null)
                        .passwordResetTokenValidUntil(null)
                        .authorizationToken(null)
                        .authorizationTokenValidUntil(null)
                        .reportCount(0)
                        .roles(MemberRoles.getDefaultFor(MemberRole.MEMBER))
                        .build());
        // 방명록 주인장
        memberRepository.save(
                Member.builder()
                        .UUID(UUID.randomUUID().toString())
                        .email(ownerEmail)
                        .nickname(ownerNickname)
                        .passwordHash(passwordHashProvider.encodePassword(guestbookPassword))
                        .gender(Gender.HIDDEN)
                        .dateOfBirth(LocalDate.now())
                        .introduction(introduction)
                        .passwordResetToken(null)
                        .passwordResetTokenValidUntil(null)
                        .authorizationToken(null)
                        .authorizationTokenValidUntil(null)
                        .reportCount(0)
                        .roles(MemberRoles.getDefaultFor(MemberRole.MEMBER))
                        .build());

        Member owner = memberRepository.findByNickname(ownerNickname).get();
        Member writer = memberRepository.findByNickname(writerNickname).get();

        guestbookRepository.save(
                Guestbook.builder()
                        .id(existId)
                        .owner(owner)
                        .writer(writer)
                        .content("구독하고 갑니다~~")
                        .reportCount(0)
                        .isGlobalBlinded(false)
                        .build());
        guestbookRepository.save(
                // 신고된 방명록
                Guestbook.builder()
                        .id(overReportedId)
                        .owner(owner)
                        .writer(writer)
                        .content("무수한 신고의 요청이..!")
                        .reportCount(Postings.reportCountThreshold)
                        .isGlobalBlinded(false)
                        .build());
    }
}
