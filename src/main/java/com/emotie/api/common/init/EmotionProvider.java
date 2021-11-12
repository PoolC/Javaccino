package com.emotie.api.common.init;

import com.emotie.api.emotion.domain.Emotion;
import com.emotie.api.emotion.repository.EmotionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;


@Order(0)
@Component
@RequiredArgsConstructor
public class EmotionProvider implements ApplicationRunner {

    public static ArrayList<String> emotionNames = new ArrayList<>();
    public static ArrayList<String> emotionColors = new ArrayList<>();

    public final static int totalEmotionNumbers = 8;
    private final EmotionRepository emotionRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        generateEmotions();
    }


    private void generateEmotions() {

        emotionNames.add("설렘");
        emotionNames.add("질투");
        emotionNames.add("놀람");
        emotionNames.add("화남");
        emotionNames.add("기쁨");
        emotionNames.add("슬픔");
        emotionNames.add("지침");
        emotionNames.add("무감정");

        emotionColors.add("#A29CB6");
        emotionColors.add("#9431A4");
        emotionColors.add("#AEE477");
        emotionColors.add("#FF855E");
        emotionColors.add("#FFF27D");
        emotionColors.add("#9FA7EF");
        emotionColors.add("#ADADAD");
        emotionColors.add("#FFFFFF");

        for (int i = 0; i < 8; i++) {
            Emotion emotion = Emotion.of(emotionNames.get(i), emotionColors.get(i));
            emotionRepository.save(emotion);
        }

    }
}
