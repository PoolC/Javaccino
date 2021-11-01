package com.emotie.api.emotion;

import com.emotie.api.AcceptanceTest;
import com.emotie.api.auth.dto.LoginRequest;
import com.emotie.api.auth.dto.LoginResponse;
import com.emotie.api.diary.dto.DiaryCreateRequest;
import com.emotie.api.emotion.domain.Emotion;
import com.emotie.api.emotion.repository.EmotionRepository;
import com.emotie.api.member.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static com.emotie.api.auth.AuthAcceptanceTest.loginRequest;
import static com.emotie.api.diary.DiaryApiTest.diaryCreateRequest;
import static com.emotie.api.emotion.EmotionDataLoader.*;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles({"EmotionDataLoader", "DiaryDataLoader"})
public class EmotionApiTest extends AcceptanceTest {
    private static final String NEW_CONTENT = "내일도 잠을 잘 잘 것이다. 좋을 것이다.";
    private static final String HAPPY_EMOTION = "happy";

    @Autowired
    private EmotionRepository emotionRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    void testScoreIsDeepenedOnDiaryPost() {
        String accessToken = login(MEMBER_EMAIL, MEMBER_PASSWORD);
        DiaryCreateRequest diaryCreateRequest = DiaryCreateRequest.builder()
                .emotion(HAPPY_EMOTION)
                .content(NEW_CONTENT)
                .isOpened(false)
                .build();
        diaryCreateRequest(accessToken, diaryCreateRequest);

        List<Emotion> emotions = emotionRepository.findAllByMember(member);
        assertThat(Math.round(emotions.stream()
                .filter(emotion -> emotion.getName().equals(HAPPY_EMOTION))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No emotion found with name: " + HAPPY_EMOTION))
                .getScore() * 100)).isEqualTo(21L);
    }

    public static String login(String email, String password) {
        LoginRequest request = LoginRequest.builder()
                .email(email)
                .password(password)
                .build();

        return loginRequest(request)
                .as(LoginResponse.class)
                .getAccessToken();
    }
}
