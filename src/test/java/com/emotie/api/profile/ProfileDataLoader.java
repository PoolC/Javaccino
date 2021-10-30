package com.emotie.api.profile;

import com.emotie.api.auth.infra.PasswordHashProvider;
import com.emotie.api.diary.domain.Diary;
import com.emotie.api.diary.repository.DiaryRepository;
import com.emotie.api.emotion.domain.Emotion;
import com.emotie.api.emotion.repository.EmotionRepository;
import com.emotie.api.member.domain.*;
import com.emotie.api.member.repository.EmotionScoreRepository;
import com.emotie.api.member.repository.FollowRepository;
import com.emotie.api.member.repository.MemberRepository;
import com.emotie.api.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

import static com.emotie.api.common.init.EmotionProvider.emotionNames;

@Order(1)
@Component
@Profile("ProfileDataLoader")
@RequiredArgsConstructor
public class ProfileDataLoader implements ApplicationRunner {

    private final MemberRepository memberRepository;
    private final PasswordHashProvider passwordHashProvider;
    private final EmotionRepository emotionRepository;
    private final DiaryRepository diaryRepository;
    private final FollowRepository followRepository;
    private final MemberService memberService;
    private final EmotionScoreRepository emotionScoreRepository;

    public static Member profileMember;

    public static String profileMemberEmail = "profileMember@gmail.com";
    public static String profileMemberEmotionEmail = "profileMemberEmotion@gmail.com";
    public static String password = "password123!@";

    public static String profileMemberId = "profileMember";
    private static String profileMemberNickname = "profile-nickname";
    private static String profileMemberIntro = "자기소개입니다.";
    private static String profileMemberIntroUpdated = "자기소개 수정했습니다.";



    private static String profileMemberEmotionId = "profileMemberEmotion";
    private static String profileMemberEmotionNickname = "emotion-nickname";

    @Override
    public void run(ApplicationArguments args) throws Exception {
        generateMembers();
        generateDiaries();
    }

    private void generateMembers(){

         profileMember = Member.builder()
                .UUID(profileMemberId)
                .email(profileMemberEmail)
                .nickname(profileMemberNickname)
                .passwordHash(passwordHashProvider.encodePassword(password))
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

        Member profileMemberEmotion = Member.builder()
                .UUID(profileMemberEmotionId)
                .email(profileMemberEmotionEmail)
                .nickname(profileMemberEmotionNickname)
                .passwordHash(passwordHashProvider.encodePassword(password))
                .gender(Gender.HIDDEN)
                .dateOfBirth(LocalDate.now())
                .introduction("test")
                .passwordResetToken(null)
                .passwordResetTokenValidUntil(null)
                .authorizationToken(null)
                .authorizationTokenValidUntil(null)
                .reportCount(0)
                .roles(MemberRoles.getDefaultFor(MemberRole.MEMBER))
                .build();

        memberRepository.save(profileMember);
        memberRepository.save(profileMemberEmotion);

        List<Emotion> allEmotion = emotionRepository.findAll();

        List.of(profileMember, profileMemberEmotion).forEach(
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

        for (int i =0; i < 4; i++){
            Member followMember = Member.builder()
                    .UUID("followMember_" + i)
                    .email("follow"+i + "@gamil.com")
                    .nickname("followMember_" + i)
                    .passwordHash(passwordHashProvider.encodePassword(password))
                    .dateOfBirth(LocalDate.now())
                    .gender(Gender.HIDDEN)
                    .introduction("test")
                    .passwordResetToken(null)
                    .passwordResetTokenValidUntil(null)
                    .authorizationToken(null)
                    .authorizationTokenValidUntil(null)
                    .reportCount(0)
                    .roles(MemberRoles.getDefaultFor(MemberRole.MEMBER))
                    .build();


            Member followeeMember = Member.builder()
                    .UUID("followeeMember_"+ i )
                    .email("followee"+i + "@gamil.com")
                    .nickname("followeeMember_" + i)
                    .dateOfBirth(LocalDate.now())
                    .passwordHash(passwordHashProvider.encodePassword(password))
                    .dateOfBirth(LocalDate.now())
                    .gender(Gender.HIDDEN)
                    .introduction("test")
                    .passwordResetToken(null)
                    .passwordResetTokenValidUntil(null)
                    .authorizationToken(null)
                    .authorizationTokenValidUntil(null)
                    .reportCount(0)
                    .roles(MemberRoles.getDefaultFor(MemberRole.MEMBER))
                    .build();

            memberRepository.save(followMember);
            memberRepository.save(followeeMember);
            followRepository.save( new Follow(profileMember, followeeMember));
            followRepository.save( new Follow(followMember, profileMember));
        }


    }

    private void generateDiaries(){
        for (int i = 0; i < 8; i++) {
            Emotion emotion = emotionRepository.findByEmotion(emotionNames.get(i)).get();

            diaryRepository.save(
                    Diary.builder()
                            .writer(profileMember)
                            .emotion(emotion)
                            .content("test")
                            .isOpened(true)
                            .build());

            memberService.deepenEmotionScore(profileMember, emotion.getEmotion());

        }
    }

}

