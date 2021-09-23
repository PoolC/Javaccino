package com.emotie.api.diary;

import com.emotie.api.emotion.domain.Emotion;
import com.emotie.api.emotion.repository.EmotionRepository;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("diaryDataLoader")
@RequiredArgsConstructor
public class DiaryDataLoader implements ApplicationRunner {
    private final EmotionRepository emotionRepository;

    public static String testEmotion, invalidEmotion;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        createEmotion();
    }

    private void createEmotion() {
        Emotion happy = Emotion.builder()
                .emotion("기쁨|HAPPY")
                .color("#FFF27D")
                .build();
        testEmotion = happy.getEmotion();
        invalidEmotion = "없음|none";
        emotionRepository.save(happy);
    }
}
