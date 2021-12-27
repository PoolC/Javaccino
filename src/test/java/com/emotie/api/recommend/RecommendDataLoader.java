package com.emotie.api.recommend;

import com.emotie.api.auth.infra.PasswordHashProvider;
import com.emotie.api.emotion.domain.Emotion;
import com.emotie.api.emotion.domain.Emotions;
import com.emotie.api.emotion.repository.EmotionRepository;
import com.emotie.api.member.domain.Gender;
import com.emotie.api.member.domain.Member;
import com.emotie.api.member.domain.MemberRole;
import com.emotie.api.member.domain.MemberRoles;
import com.emotie.api.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Profile("recommendDataLoader")
@RequiredArgsConstructor
public class RecommendDataLoader implements ApplicationRunner {
    private final MemberRepository memberRepository;
    private final PasswordHashProvider passwordHashProvider;
    private final EmotionRepository emotionRepository;

    private static final String EMOTION_BASE_PACKAGE = "com.emotie.api.emotion";

    private static final String CANDIDATE_EMAIL_HEAD = "candidateEmail";
    private static final String CANDIDATE_EMAIL_TAIL = "@gmail.com";
    private static final String CANDIDATE_NICKNAME_TEMPLATE = "candidateNickname";
    private static final String CANDIDATE_PASSWORD_TEMPLATE = "candidatePassword";
    private static final String INTRODUCTION = "랜덤 자기소개";

    private static final String LEADER_EMAIL_HEAD = "leaderEmail";
    private static final String LEADER_EMAIL_TAIL = "@gmail.com";
    private static final String LEADER_NICKNAME_TEMPLATE = "leaderNickname";
    private static final String LEADER_PASSWORD_TEMPLATE = "leaderPassword";

    private static final Integer MAX_DIARY_COUNT = 300;

    public static final String GROUP_PURE_HAPPY = "HAPPY_PURE";
    public static final String GROUP_PURE_SAD = "SAD_PURE";
    public static final String GROUP_PURE_TIRED = "TIRED_PURE";

    public static final String GROUP_MAJOR_HAPPY = "HAPPY_MAJOR";
    public static final String GROUP_MAJOR_SAD = "SAD_MAJOR";
    public static final String GROUP_MAJOR_TIRED = "TIRED_MAJOR";

    public static final String GROUP_MINOR_HAPPY = "HAPPY_MINOR";
    public static final String GROUP_MINOR_SAD = "SAD_MINOR";
    public static final String GROUP_MINOR_TIRED = "TIRED_MINOR";

    @Override
    public void run(ApplicationArguments args) throws Exception {
        createPureGroup(15, 3, 10);
        createMajorGroup(15, 3, 10);
        createMinorGroup(15, 3 ,10);
    }

    private void createPureGroup(Integer candidateCount, Integer randomCountRange, Integer baseSeed) {
        List<String> testEmotion = List.of("기쁨", "슬픔", "지침");
        Map<String, String> testEmotionToGroup = Map.of(
                "기쁨", GROUP_PURE_HAPPY,
                "슬픔", GROUP_PURE_SAD,
                "지침", GROUP_PURE_TIRED
        );
        for (String emotion: testEmotion) {
            Map<String, Integer> pureCountMap = defaultCountMap();
            pureCountMap.put(emotion, MAX_DIARY_COUNT);

            createGroup(
                    testEmotionToGroup.get(emotion), pureCountMap, candidateCount, randomCountRange, baseSeed
            );
        }
    }

    private void createMajorGroup(Integer candidateCount, Integer randomCountRange, Integer baseSeed) {
        List<String> testEmotion = List.of("기쁨", "슬픔", "지침");
        Map<String, String> testEmotionToGroup = Map.of(
                "기쁨", GROUP_MAJOR_HAPPY,
                "슬픔", GROUP_MAJOR_SAD,
                "지침", GROUP_MAJOR_TIRED
        );
        for (String emotion: testEmotion) {
            Map<String, Integer> majorCountMap = defaultCountMap();
            majorCountMap.replaceAll(
                    (emotionName, stdCount) -> {
                        if (emotionName.equals(emotion)) {
                            return (int)(0.6 * MAX_DIARY_COUNT);
                        }
                        return (int)(0.4 * MAX_DIARY_COUNT / 7);
                    }
            );

            createGroup(
                    testEmotionToGroup.get(emotion), majorCountMap, candidateCount, randomCountRange, baseSeed
            );
        }
    }

    private void createMinorGroup(Integer candidateCount, Integer randomCountRange, Integer baseSeed) {
        List<String> testEmotion = List.of("기쁨", "슬픔", "지침");
        Map<String, String> testEmotionToGroup = Map.of(
                "기쁨", GROUP_MINOR_HAPPY,
                "슬픔", GROUP_MINOR_SAD,
                "지침", GROUP_MINOR_TIRED
        );
        for (String emotion: testEmotion) {
            Map<String, Integer> majorCountMap = defaultCountMap();
            majorCountMap.replaceAll(
                    (emotionName, stdCount) -> {
                        if (emotionName.equals(emotion)) {
                            return (int)(0.3 * MAX_DIARY_COUNT);
                        }
                        return (int)(0.1 * MAX_DIARY_COUNT);
                    }
            );

            createGroup(
                    testEmotionToGroup.get(emotion), majorCountMap, candidateCount, randomCountRange, baseSeed
            );
        }
    }

    private void createGroup(
            String groupName, Map<String, Integer> emotionCountMap, Integer candidateCount,
            Integer randomCountRange, Integer baseSeed
    ) {
        Member leader = Member.builder()
                .UUID(UUID.randomUUID().toString())
                .email(getLeaderInfo(groupName, "email"))
                .nickname(getLeaderInfo(groupName, "nickname"))
                .passwordHash(passwordHashProvider.encodePassword(getLeaderInfo(groupName, "password")))
                .gender(Gender.HIDDEN)
                .dateOfBirth(LocalDate.now())
                .introduction(INTRODUCTION)
                .passwordResetToken(null)
                .passwordResetTokenValidUntil(null)
                .authorizationToken(null)
                .authorizationTokenValidUntil(null)
                .reportCount(0)
                .roles(MemberRoles.getDefaultFor(MemberRole.MEMBER))
                .build();
        memberRepository.saveAndFlush(leader);

        createEmotions(leader);
        setMemberEmotionWithRandomDifference(leader, emotionCountMap, 1, baseSeed);
        // randomCountRange 가 1이라 difference 가 없음.

        for (int i = 0; i < candidateCount; i++) {
            Member candidate = Member.builder()
                    .UUID(UUID.randomUUID().toString())
                    .email(getCandidateInfo(groupName, "email", i))
                    .nickname(getCandidateInfo(groupName, "nickname", i))
                    .passwordHash(passwordHashProvider.encodePassword(getCandidateInfo(groupName, "password", i)))
                    .gender(Gender.HIDDEN)
                    .dateOfBirth(LocalDate.now())
                    .introduction(INTRODUCTION)
                    .passwordResetToken(null)
                    .passwordResetTokenValidUntil(null)
                    .authorizationToken(null)
                    .authorizationTokenValidUntil(null)
                    .reportCount(0)
                    .roles(MemberRoles.getDefaultFor(MemberRole.MEMBER))
                    .build();
            memberRepository.saveAndFlush(candidate);

            createEmotions(candidate);
            setMemberEmotionWithRandomDifference(candidate, emotionCountMap, randomCountRange, baseSeed + i);
        }
    }

    public static String getCandidateInfo(String groupName, String type, Integer index) {
        if (type.equals("email")) {
            return groupName + "-" + CANDIDATE_EMAIL_HEAD + index + CANDIDATE_EMAIL_TAIL;
        }
        if (type.equals("nickname")) {
            return groupName + "-" + CANDIDATE_NICKNAME_TEMPLATE + index;
        }
        if (type.equals("password")) {
            return groupName + "-" + CANDIDATE_PASSWORD_TEMPLATE + index;
        }
        return "";
    }

    public static String getLeaderInfo(String groupName, String type) {
        if (type.equals("email")) {
            return groupName + "-" + LEADER_EMAIL_HEAD + LEADER_EMAIL_TAIL;
        }
        if (type.equals("nickname")) {
            return groupName + "-" + LEADER_NICKNAME_TEMPLATE;
        }
        if (type.equals("password")) {
            return groupName + "-" + LEADER_PASSWORD_TEMPLATE;
        }
        return "";
    }

    private void setMemberEmotionWithRandomDifference(
            Member member, Map<String, Integer> emotionCountMap, Integer randomCountRange, Integer seed
    ) {
        Emotions memberEmotions = new Emotions(member, emotionRepository.findAllByMember(member));
        emotionCountMap.forEach(
                (emotionName, stdCount) -> {
                    Random randomizer = new Random(seed);
                    int deepenEmotionCount = stdCount + randomizer.nextInt(randomCountRange);
                    for (int i = 0; i < deepenEmotionCount; i++) {
                        memberEmotions.deepenCurrentEmotionScore(emotionName);
                    }
                }
        );
        emotionRepository.saveAllAndFlush(memberEmotions.allMemberEmotions());
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

    private Map<String, Integer> defaultCountMap() {
        Map<String, Integer> defaultCountMap = new HashMap<>();
        defaultCountMap.put("기쁨", 0);
        defaultCountMap.put("슬픔", 0);
        defaultCountMap.put("지침", 0);
        defaultCountMap.put("설렘", 0);
        defaultCountMap.put("질투", 0);
        defaultCountMap.put("놀람", 0);
        defaultCountMap.put("화남", 0);
        defaultCountMap.put("무감정", 0);
        return defaultCountMap;
    }
}
