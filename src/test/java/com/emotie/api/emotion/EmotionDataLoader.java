package com.emotie.api.emotion;


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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

@Component
@Profile("EmotionDataLoader")
@RequiredArgsConstructor
public class EmotionDataLoader implements ApplicationRunner {

    private final MemberRepository memberRepository;
    private final PasswordHashProvider passwordHashProvider;
    private final EmotionRepository emotionRepository;
    private final DiaryRepository diaryRepository;

    public static String
            adminEmail = "admin@gmail.com",
            password =  "password123!";
    private String introduction = "안녕하세요";

    public static ArrayList<String> emotionNames = new ArrayList<>();
    public static ArrayList<String> emotionColors = new ArrayList<>();

    public static Integer updatingEmotionId;
    public static String beforeUpdatingEmotion;
    public static Integer deletingSuccessEmotionId;
    public static Integer deletingFailEmotionId;

    @Transactional
    @Override
    public void run(ApplicationArguments args) throws Exception {

        generateAdmin();
        generateEmotions();

    }

    private void generateAdmin(){

        memberRepository.save(
                Member.builder()
                        .UUID(UUID.randomUUID().toString())
                        .email(adminEmail)
                        .nickname(adminEmail)
                        .passwordHash(passwordHashProvider.encodePassword(password))
                        .gender(Gender.HIDDEN)
                        .dateOfBirth(LocalDate.now())
                        .introduction(introduction)
                        .passwordResetToken(null)
                        .passwordResetTokenValidUntil(LocalDateTime.now().minusDays(1))
                        .authorizationToken(null)
                        .authorizationTokenValidUntil(null)
                        .reportCount(0)
                        .roles(MemberRoles.getDefaultFor(MemberRole.ADMIN))
                        .build());

    }

    private void generateEmotions(){

        emotionNames.add("설렘|FLUTTER");
        emotionNames.add("질투|JEALOUS");
        emotionNames.add("놀람|SURPRISED");
        emotionNames.add("화남|ANGRY");
        emotionNames.add("기쁨|HAPPY");
        emotionNames.add("슬픔|SAD");
        emotionNames.add("지침|TIRED");
        emotionNames.add("무감정|NONE");

        emotionColors.add("#A29CB6");
        emotionColors.add("#9431A4");
        emotionColors.add("#AEE477");
        emotionColors.add("#FF855E");
        emotionColors.add("#FFF27D");
        emotionColors.add("#9FA7EF");
        emotionColors.add("#ADADAD");
        emotionColors.add("#FFFFFF");

        for (int i = 0; i < 8; i++){
            Emotion emotion = Emotion.of(emotionNames.get(i), emotionColors.get(i));
            if ( i < 7) {
                Diary diary = new Diary(LocalDate.now(), Member.builder().build(), "s", emotion, false);
                diary.setEmotion(emotion);
                diaryRepository.save(diary);
                emotion.getDiariesList().add(diary);
            }
            emotionRepository.saveAndFlush(emotion);
        }

        updatingEmotionId = emotionRepository.findByEmotion("슬픔|SAD").orElseThrow().getId();
        beforeUpdatingEmotion = "슬픔|SAD";

        deletingFailEmotionId = emotionRepository.findByEmotion("지침|TIRED").orElseThrow().getId();
        deletingSuccessEmotionId = emotionRepository.findByEmotion("무감정|NONE").orElseThrow().getId();
        System.out.println("deletingFailEmotionId = " + deletingFailEmotionId);
        System.out.println("emotionRepository.findByEmotion(\"지침|TIRED\").orElseThrow().getDiariesList().size() = " + emotionRepository.findById(deletingFailEmotionId).orElseThrow().getDiariesList().size());
        System.out.println("deletingSuccessEmotionId = " + deletingSuccessEmotionId);
        System.out.println(" emotionRepository.findByEmotion(\"무감정|NONE\").orElseThrow().getDiariesList().size() = " + emotionRepository.findById(deletingSuccessEmotionId).orElseThrow().getDiariesList().size());

    }
}
