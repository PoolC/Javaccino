package com.emotie.api.guestbook;

import com.emotie.api.guestbook.domain.Guestbook;
import com.emotie.api.guestbook.repository.GuestbookRepository;
import com.emotie.api.member.MemberDataLoader;
import com.emotie.api.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("guestbookDataLoader")
@RequiredArgsConstructor
public class GuestbookDataLoader implements CommandLineRunner {
    private final GuestbookRepository guestbookRepository;

    private static MemberRepository memberRepository;

    public static String unauthorizedUUID = memberRepository.findByNickname(MemberDataLoader.unauthorizedEmail).get().getUUID(),
            authorizedUUID = memberRepository.findByNickname(MemberDataLoader.authorizedEmail).get().getUUID();
    public static Integer existId = 1, reportedId = 2, notExistId = 3;

    @Override
    public void run(String... args) throws Exception {
        guestbookRepository.save(
                // unauth 유저가 auth 유저에게 남긴 방명록
                Guestbook.builder()
                        .id(existId)
                        .ownerId(authorizedUUID)
                        .writerId(unauthorizedUUID)
                        .content("구독하고 갑니다~~")
                        .reportCount(0)
                        .build());
        guestbookRepository.save(
                // 신고된 방명록
                Guestbook.builder()
                        .id(reportedId)
                        .ownerId(authorizedUUID)
                        .writerId(unauthorizedUUID)
                        .content("무수한 신고의 요청이..!")
                        .reportCount(5)
                        .build());
    }
}
