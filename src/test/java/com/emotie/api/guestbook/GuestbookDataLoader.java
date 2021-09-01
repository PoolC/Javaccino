package com.emotie.api.guestbook;

import com.emotie.api.auth.infra.PasswordHashProvider;
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

@Component
@Profile("guestbookDataLoader")
@RequiredArgsConstructor
public class GuestbookDataLoader implements CommandLineRunner {
    private final GuestbookRepository guestbookRepository;
    private final PasswordHashProvider passwordHashProvider;
    private final MemberRepository memberRepository;

    public static String writerUUID = "1a076897-14c7-4f76-986c-63bb6e03151c",
            ownerUUID = "c211ffed-ee58-49f9-8c64-a28777446826",
            writerEmail = "writer@naver.com",
            ownerEmail = "owner@naver.com",
            writerNickname = "Writer Kim",
            ownerNickname = "Owner Park",
            notExistNickname = "Not exist nickname",
            guestbookPassword = "qhdks1!",
            introduction = "We are Guestbook test users",
            createContent = "구독하고 갑ㄴ디ㅏ",
            changedContent = "구독하고 갑니다";

    public static Integer existId = 1, reportedId = 2, notExistId = -1;

    @Override
    public void run(String... args) throws Exception {
        // 방명록 작성자
        memberRepository.save(
                Member.builder()
                        .UUID(writerUUID)
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
                        .UUID(ownerUUID)
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
        guestbookRepository.save(
                Guestbook.builder()
                        .id(existId)
                        .ownerId(ownerUUID)
                        .writerId(writerUUID)
                        .content("구독하고 갑니다~~")
                        .reportCount(0)
                        .build());
        guestbookRepository.save(
                // 신고된 방명록
                Guestbook.builder()
                        .id(reportedId)
                        .ownerId(ownerUUID)
                        .writerId(writerUUID)
                        .content("무수한 신고의 요청이..!")
                        .reportCount(5)
                        .build());
    }
}
