package com.emotie.api.diary;

import com.emotie.api.auth.infra.PasswordHashProvider;
import com.emotie.api.diary.domain.Diary;
import com.emotie.api.diary.dto.DiaryReportRequest;
import com.emotie.api.diary.repository.DiaryRepository;
import com.emotie.api.diary.service.DiaryService;
import com.emotie.api.emotion.domain.Emotion;
import com.emotie.api.emotion.domain.Emotions;
import com.emotie.api.emotion.repository.EmotionRepository;
import com.emotie.api.member.domain.Gender;
import com.emotie.api.member.domain.Member;
import com.emotie.api.member.domain.MemberRole;
import com.emotie.api.member.domain.MemberRoles;
import com.emotie.api.member.repository.FollowRepository;
import com.emotie.api.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@SuppressWarnings("FieldCanBeLocal")
@Order(0)
@Component
@Profile("diaryDataLoader")
@RequiredArgsConstructor
public class DiaryDataLoader implements ApplicationRunner {
    private final EmotionRepository emotionRepository;
    private final DiaryRepository diaryRepository;
    private final MemberRepository memberRepository;
    private final PasswordHashProvider passwordHashProvider;
    private final FollowRepository followRepository;
    private final DiaryService diaryService;

    public static final String writerEmail = "writer@gmail.com";
    public static final String viewerEmail = "viewer@gmail.com";
    public static final String unauthorizedEmail = "unauthorized@gmail.com";
    public static final String reporterEmail = "reporter@gmail.com";
    public static final String writerNickname = "공릉동공룡";
    public static final String viewerNickname = "공릉동익룡";
    public static final String reporterNickname = "번째 신고자";
    public static final String unauthorizedNickname = "공릉동도롱뇽";
    private static final String introduction = "사람들에게 자신을 소개해 보세요!";
    public static final String password = "random-password";

    public static Member writer, viewer, unauthorized;
    public static String writerId;
    public static String notExistMemberId = "FakeID";

    public static final String originalContent = "오늘 잠을 잘 잤다. 좋았다.";
    public static final String updatedContent = "어제도 잠을 잘 잤다. 좋았었다.";
    public static final String newContent = "내일도 잠을 잘 잘 것이다. 좋을 것이다.";
    public static final Long invalidId = Long.MAX_VALUE;

    public static Emotion diaryEmotion, otherEmotion;
    public static Long openedDiaryId, closedDiaryId, viewerReportedId,
            unreportedId, almostReportedId, overReportedId, unBlindedId, viewerBlindedId;
    public static Long diaryCount;

    public static Member[] reporters = new Member[Diary.reportCountThreshold];
    public static String reportReason = "신고 테스트를 하고 싶어서";

    public static Map<String, Double> writerEmotionScores = new HashMap<>();

    private static final String EMOTION_BASE_PACKAGE = "com.emotie.api.emotion";

    @Override
    public void run(ApplicationArguments args) throws Exception {
        registerMembers();
        loadEmotion();
        writeDiaries();
        registerReporters();
        writeDiariesAndReport();
        writeDiariesAndBlind();
        loadScores();
        loadInfo();
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

        List<Member> members = List.of(writer, viewer, unauthorized);

        for (Member member: members) {
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
        members.forEach(memberRepository::saveAndFlush);
        writerId = writer.getUUID();
    }

    private void loadEmotion() {
        diaryEmotion = emotionRepository.findByMemberAndName(writer, "happy").get();
        otherEmotion = emotionRepository.findByMemberAndName(writer, "sad").get();
    }

    private void writeDiaries() {
        Diary openedDiary = Diary.of(
                writer,
                originalContent,
                diaryEmotion,
                true
        );
        diaryRepository.save(openedDiary);
        openedDiaryId = openedDiary.getId();

        Diary closedDiary = Diary.of(
                writer,
                originalContent,
                diaryEmotion,
                false
        );
        diaryRepository.save(closedDiary);
        closedDiaryId = closedDiary.getId();

        for (int i = 0; i < 2; i++) {
            deepenDiaryEmotion();
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
                deepenOtherEmotion();

            } else {
                diaryRepository.save(
                        Diary.of(
                                writer,
                                originalContent + i,
                                diaryEmotion,
                                openFlag
                        )
                );
                deepenDiaryEmotion();
            }
        }
    }

    private void loadScores() {
        emotionRepository.findAllByMember(writer).forEach(
                emotion -> writerEmotionScores.put(emotion.getName(), emotion.getScore())
        );
    }

    private void loadInfo() {
        diaryCount = (long) diaryRepository.findAll().size();
    }

    private void deepenDiaryEmotion() {
        Emotions emotions = new Emotions(writer, emotionRepository.findAllByMember(writer));
        emotions.deepenCurrentEmotionScore(diaryEmotion.getName());
        emotionRepository.saveAllAndFlush(emotions.allMemberEmotions());
    }

    private void deepenOtherEmotion() {
        Emotions emotions = new Emotions(writer, emotionRepository.findAllByMember(writer));
        emotions.deepenCurrentEmotionScore(otherEmotion.getName());
        emotionRepository.saveAllAndFlush(emotions.allMemberEmotions());
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
        deepenDiaryEmotion();
        viewerReportedId = diaryRepository.save(
                Diary.of(
                        writer,
                        originalContent,
                        diaryEmotion,
                        true
                )
        ).getId();
        deepenDiaryEmotion();
        diaryService.report(viewer, DiaryReportRequest.builder().reason(reportReason).build(), viewerReportedId);

        almostReportedId = diaryRepository.save(
                Diary.of(
                        writer,
                        originalContent,
                        diaryEmotion,
                        true
                )
        ).getId();
        deepenDiaryEmotion();
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
        deepenDiaryEmotion();
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
        deepenDiaryEmotion();

        viewerBlindedId = diaryRepository.save(
                Diary.of(
                        writer,
                        originalContent,
                        diaryEmotion,
                        true
                )
        ).getId();
        deepenDiaryEmotion();
        diaryService.blind(viewer, viewerBlindedId);
    }
}
