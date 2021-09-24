package com.emotie.api.diary;

import com.emotie.api.auth.infra.PasswordHashProvider;
import com.emotie.api.diary.domain.Diary;
import com.emotie.api.diary.repository.DiaryRepository;
import com.emotie.api.emotion.domain.Emotion;
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

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("FieldCanBeLocal")
@Component
@Profile("diaryDataLoader")
@RequiredArgsConstructor
public class DiaryDataLoader implements ApplicationRunner {
    private final EmotionRepository emotionRepository;
    private final DiaryRepository diaryRepository;
    private final MemberRepository memberRepository;
    private final PasswordHashProvider passwordHashProvider;

    public static String testEmotion, invalidEmotion;

    public static final String writerEmail = "writer@gmail.com",
            viewerEmail = "viewer@gmail.com",
            unauthorizedEmail = "unauthorized@gmail.com";
    public static final String writerNickname = "공릉동공룡",
            viewerNickname = "공릉동익룡",
            unauthorizedNickname = "공릉동도롱뇽",
            notExistNickname = "공릉동용용";
    private static final String introduction = "사람들에게 자신을 소개해 보세요!";
    public static final String password = "random-password";

    private static Member writer, viewer, unauthorized;

    public static final String originalContent = "오늘 잠을 잘 잤다. 좋았다.",
            updatedContent = "어제도 잠을 잘 잤다. 좋았었다.",
            newContent = "내일도 잠을 잘 잘 것이다. 좋을 것이다.";
    public static final Integer invalidId = Integer.MAX_VALUE;

    public static Emotion diaryEmotion;

    public static Integer openedDiaryId, closedDiaryId;

    public static Long diaryCount;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        registerMembers();
        createEmotions();
        writeDiaries();
        setDiaryIndexes();
    }

    private void createEmotions() {
        diaryEmotion = Emotion.builder()
                .emotion("기쁨|HAPPY")
                .color("#FFF27D")
                .build();
        testEmotion = diaryEmotion.getEmotion();
        invalidEmotion = "없음|none";
        emotionRepository.save(diaryEmotion);
    }

    private void registerMembers() {
        writer = Member.builder()
                .UUID(UUID.randomUUID().toString())
                .email(writerEmail)
                .nickname(writerNickname)
                .passwordHash(passwordHashProvider.encodePassword(password))
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
        viewer = Member.builder()
                .UUID(UUID.randomUUID().toString())
                .email(viewerEmail)
                .nickname(viewerNickname)
                .passwordHash(passwordHashProvider.encodePassword(password))
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
        unauthorized = Member.builder()
                .UUID(UUID.randomUUID().toString())
                .email(unauthorizedEmail)
                .nickname(unauthorizedNickname)
                .passwordHash(passwordHashProvider.encodePassword(password))
                .gender(Gender.HIDDEN)
                .dateOfBirth(LocalDate.now())
                .introduction(introduction)
                .passwordResetToken(null)
                .passwordResetTokenValidUntil(null)
                .authorizationToken(null)
                .authorizationTokenValidUntil(null)
                .reportCount(0)
                .roles(MemberRoles.getDefaultFor(MemberRole.UNACCEPTED))
                .build();
        memberRepository.saveAllAndFlush(List.of(writer, viewer, unauthorized));
    }

    private void writeDiaries() {
        diaryRepository.save(
                Diary.of(
                        LocalDate.now(),
                        writer,
                        originalContent,
                        diaryEmotion,
                        true
                        )
        );
        emotionRepository.saveAndFlush(diaryEmotion);
        diaryRepository.save(
                Diary.of(
                        LocalDate.now(),
                        writer,
                        originalContent,
                        diaryEmotion,
                        false
                )
        );
        emotionRepository.saveAndFlush(diaryEmotion);
        for (int i = 0; i < 2; i++) {
            writer.deepenEmotionScore(diaryEmotion);
        }
        memberRepository.saveAndFlush(writer);
    }

    private void setDiaryIndexes() {
        diaryRepository.findAll().forEach(
                (it) -> {
                    if (it.getIsOpened()) openedDiaryId = it.getId();
                    else closedDiaryId = it.getId();
                }
        );
        diaryCount = diaryRepository.count();
    }
}
