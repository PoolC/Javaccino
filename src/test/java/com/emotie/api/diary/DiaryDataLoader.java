package com.emotie.api.diary;

import com.emotie.api.auth.infra.PasswordHashProvider;
import com.emotie.api.diary.domain.Diary;
import com.emotie.api.diary.dto.DiaryReportRequest;
import com.emotie.api.diary.repository.DiaryRepository;
import com.emotie.api.diary.service.DiaryService;
import com.emotie.api.emotion.domain.Emotion;
import com.emotie.api.emotion.repository.EmotionRepository;
import com.emotie.api.member.domain.*;
import com.emotie.api.member.repository.EmotionScoreRepository;
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
    private final EmotionScoreRepository emotionScoreRepository;
    private final PasswordHashProvider passwordHashProvider;
    private final DiaryService diaryService;


    public static String testEmotion, invalidEmotion;

    public static final String writerEmail = "writer@gmail.com";
    public static final String viewerEmail = "viewer@gmail.com";
    public static final String unauthorizedEmail = "unauthorized@gmail.com";
    public static final String reporterEmail = "reporter@gmail.com";
    public static final String writerNickname = "공릉동공룡";
    public static final String viewerNickname = "공릉동익룡";
    public static final String reporterNickname = "번째 신고자";
    public static final String unauthorizedNickname = "공릉동도롱뇽";
    public static final String notExistNickname = "공릉동용용";
  
    private static final String introduction = "사람들에게 자신을 소개해 보세요!";
    public static final String password = "random-password";

    private static Member writer, viewer, unauthorized;
    public static String writerId;
    public static final String notExistMemberId = "notExist!#";

    public static final String originalContent = "오늘 잠을 잘 잤다. 좋았다.",
            updatedContent = "어제도 잠을 잘 잤다. 좋았었다.",
            newContent = "내일도 잠을 잘 잘 것이다. 좋을 것이다.";
    public static final Long invalidId = Long.MAX_VALUE;
    public static final int PAGE_SIZE = 10;

    public static Emotion diaryEmotion, otherEmotion;

    public static Double basicDiaryEmotionScore, basicOtherEmotionScore;
    public static Integer basicDiaryEmotionCount, basicOtherEmotionCount;

    public static Long openedDiaryId, closedDiaryId, viewerReportedId, unreportedId, almostReportedId, overReportedId, unBlindedId, viewerBlindedId;
    public static Long diaryCount;

    public static Member[] reporters = new Member[Diary.reportCountThreshold];
    public static String reportReason = "신고 테스트를 하고 싶어서";

    @Override
    public void run(ApplicationArguments args) throws Exception {
        createEmotions();
        registerMembers();
        writeDiaries();
        countDiaries();
    }

    private void createEmotions() {
        diaryEmotion = Emotion.builder()
                .emotion("기쁨|HAPPY")
                .color("#FFF27D")
                .build();
        testEmotion = diaryEmotion.getEmotion();
        invalidEmotion = "없음|none";
        emotionRepository.save(diaryEmotion);

        otherEmotion = Emotion.builder()
                .emotion("슬픔|SAD")
                .color("#9FA7EF")
                .build();
        emotionRepository.save(otherEmotion);
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

        List<Emotion> allEmotion = emotionRepository.findAll();

        List.of(writer, viewer, unauthorized).forEach(
                (user) ->
                allEmotion.forEach(
                        (emotion) -> {
                            EmotionScore emotionScore = EmotionScore.of(
                                    user.getUUID(),
                                    emotion,
                                    0.0
                            );
                            emotionScoreRepository.save(emotionScore);

                            user.initializeEmotionScore(emotion, emotionScore);
                            memberRepository.saveAndFlush(user);
                        }
                )
        );
        writerId = writer.getUUID();
    }

    private void writeDiaries() {
        Diary openedDiary = Diary.of(
                writer,
                originalContent,
                diaryEmotion,
                true
        );
        diaryRepository.save(
                openedDiary
        );
        emotionRepository.saveAndFlush(diaryEmotion);
        openedDiaryId = openedDiary.getId();

        Diary closedDiary = Diary.of(
                writer,
                originalContent,
                diaryEmotion,
                false
        );
        diaryRepository.save(
            closedDiary
        );
        emotionRepository.saveAndFlush(diaryEmotion);
        closedDiaryId = closedDiary.getId();

        for (int i = 0; i < 2; i++) {
            writer.deepenEmotionScore(diaryEmotion);
        }

        for (int i = 0; i < 95; i++) {
            Boolean openFlag = (i % 3 == 0);
            if (i % 2 == 0) {
                diaryRepository.save(
                        Diary.of(
                                writer,
                                originalContent + i,
                                otherEmotion,
                                openFlag
                        )
                );
                emotionRepository.saveAndFlush(otherEmotion);
                writer.deepenEmotionScore(otherEmotion);
            } else {
                diaryRepository.save(
                        Diary.of(
                                writer,
                                originalContent + i,
                                diaryEmotion,
                                openFlag
                        )
                );
                emotionRepository.saveAndFlush(diaryEmotion);
                writer.deepenEmotionScore(diaryEmotion);
            }

        }

        memberRepository.saveAndFlush(writer);
        basicDiaryEmotionScore = emotionScoreRepository.findByMemberIdAndEmotion(writerId, diaryEmotion).get().getScore();
        basicOtherEmotionScore = emotionScoreRepository.findByMemberIdAndEmotion(writerId, otherEmotion).get().getScore();
        basicDiaryEmotionCount = emotionScoreRepository.findByMemberIdAndEmotion(writerId, diaryEmotion).get().getCount();
        basicOtherEmotionCount = emotionScoreRepository.findByMemberIdAndEmotion(writerId, otherEmotion).get().getCount();
    }

    private void countDiaries() {
        diaryCount = diaryRepository.count();
    }

    private void registerReporters() {
        for (int i = 0; i < Diary.reportCountThreshold; i++) {
            reporters[i] = Member.builder()
                    .UUID(UUID.randomUUID().toString())
                    .email(i + reporterEmail)
                    .nickname(i + reporterNickname)
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
            memberRepository.save(reporters[i]);
        }
    }

    private void writeDiariesAndReport() {
        unreportedId = diaryRepository.save(
                Diary.of(
                        writer,
                        originalContent,
                        diaryEmotion,
                        true
                )
        ).getId();
        viewerReportedId = diaryRepository.save(
                Diary.of(
                        writer,
                        originalContent,
                        diaryEmotion,
                        true
                )
        ).getId();
        diaryService.report(viewer, DiaryReportRequest.builder().reason(reportReason).build(), viewerReportedId);

        almostReportedId = diaryRepository.save(
                Diary.of(
                        writer,
                        originalContent,
                        diaryEmotion,
                        true
                )
        ).getId();
        for (int i = 0; i < Diary.reportCountThreshold - 1; i++) {
            diaryService.report(reporters[i], DiaryReportRequest.builder().reason(reportReason).build(), almostReportedId);
        }

        overReportedId = diaryRepository.save(
                Diary.of(
                        writer,
                        originalContent,
                        diaryEmotion,
                        true
                )
        ).getId();
        for (int i = 0; i < Diary.reportCountThreshold; i++) {
            diaryService.report(reporters[i], DiaryReportRequest.builder().reason(reportReason).build(), overReportedId);
        }
    }

    private void writeDiariesAndBlind() {
        unBlindedId = diaryRepository.save(
                Diary.of(
                        writer,
                        originalContent,
                        diaryEmotion,
                        true
                )
        ).getId();

        viewerBlindedId = diaryRepository.save(
                Diary.of(
                        writer,
                        originalContent,
                        diaryEmotion,
                        true
                )
        ).getId();
        diaryService.blind(viewer, viewerBlindedId);
    }
}
