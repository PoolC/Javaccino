package com.emotie.api.guestbook;

import com.emotie.api.auth.infra.PasswordHashProvider;
import com.emotie.api.guestbook.domain.Guestbook;
import com.emotie.api.guestbook.repository.GuestbookRepository;
import com.emotie.api.guestbook.service.GuestbookService;
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
    private final GuestbookService guestbookService;

    public static String
            writerEmail = "writer@emotie.com",
            ownerEmail = "owner@emotie.com",
            reporterEmail = "reporter@emotie.com",
            writerNickname = "Writer Kim",
            ownerNickname = "Owner Park",
            reporterNickname = "reporter no. ",
            notExistNickname = "Not exist nickname",
            guestbookPassword = "qhdks1!",
            introduction = "We are Guestbook test users",
            createContent = "구독하고 갑ㄴ디ㅏ",
            changedContent = "구독하고 갑니다";

    public static Long existId, almostReportedId, overReportedId, notExistId = -1L, globalBlindedId;
    public static Member writer, owner;
    public static Member[] reporters = new Member[Guestbook.reportCountThreshold];

    @Override
    public void run(String... args) throws Exception {
        /*
        Member 생성
         */
        writer = Member.builder()
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
                .build();
        memberRepository.save(writer);

        owner = Member.builder()
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
                .build();
        memberRepository.save(owner);

        // 신고자
        for (int i = 0; i < Guestbook.reportCountThreshold; i++) {
            reporters[i] = Member.builder()
                    .UUID(UUID.randomUUID().toString())
                    .email(i + reporterEmail)
                    .nickname(reporterNickname + i)
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
                    .build();
            memberRepository.save(reporters[i]);
        }

        /*
        방명록
         */

        // 일반 방명록
        existId = guestbookRepository.save(
                Guestbook.builder()
                        .owner(owner)
                        .writer(writer)
                        .content("구독하고 갑니다~~")
                        .reportCount(0)
                        .isGlobalBlinded(false)
                        .build()).getId();
        guestbookService.toggleReport(owner, existId);

        // 신고 과다 직전 방명록
        almostReportedId = guestbookRepository.save(
                Guestbook.builder()
                        .owner(owner)
                        .writer(writer)
                        .content("신고 직전의 게시물")
                        .reportCount(0)
                        .isGlobalBlinded(false)
                        .build()).getId();
        for (int i = 0; i < Guestbook.reportCountThreshold - 1; i++) {
            guestbookService.toggleReport(reporters[i], almostReportedId);
        }

        // 신고 과다 방명록
        overReportedId = guestbookRepository.save(
                Guestbook.builder()
                        .owner(owner)
                        .writer(writer)
                        .content("신고가 너무 많아서 숨겨지게 될 방명록")
                        .reportCount(0)
                        .isGlobalBlinded(false)
                        .build()).getId();
        for (int i = 0; i < 10; i++) {
            guestbookService.toggleReport(reporters[i], overReportedId);
        }

        // 주인장이 직접 가린 방명록
        globalBlindedId = guestbookRepository.save(
                Guestbook.builder()
                        .owner(owner)
                        .writer(writer)
                        .content("주인장이 직접 가린 방명록")
                        .reportCount(0)
                        .isGlobalBlinded(true)
                        .build()).getId();
    }
}
