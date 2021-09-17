package com.emotie.api.emotion.service;

import com.emotie.api.emotion.domain.Emotion;
import com.emotie.api.emotion.dto.EmotionCreateRequest;
import com.emotie.api.emotion.dto.EmotionResponse;
import com.emotie.api.emotion.dto.EmotionUpdateRequest;
import com.emotie.api.emotion.exception.EmotionDeleteConflictException;
import com.emotie.api.emotion.repository.EmotionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class EmotionService {

    private final EmotionRepository emotionRepository;

    public List<Emotion> getAllEmotions(){
        return emotionRepository.findAll();
    }

    @Transactional
    public void createEmotion(EmotionCreateRequest request) {
        Emotion emotion = Emotion.of(request.getEmotion(), request.getColor());
        emotionRepository.save(emotion);
    }

    @Transactional
    public void updateEmotion(EmotionUpdateRequest request, Integer emotionId){
        Emotion updatingEmotion = emotionRepository.findById(emotionId).orElseThrow(()-> new NoSuchElementException("잘못된 emotionId 입니다."));
        updatingEmotion.update(request.getEmotion(), request.getColor());
        emotionRepository.saveAndFlush(updatingEmotion);
    }

    @Transactional
    public void deleteEmotion(Integer emotionId){
        Emotion deletingEmotion = emotionRepository.findById(emotionId).orElseThrow(()-> new NoSuchElementException("잘못된 emotionId 입니다."));
        if(deletingEmotion.getDiariesList().size() > 0){
            throw new EmotionDeleteConflictException("해당 감정을 사용하는 다이어리가 존재합니다.");
        }
        emotionRepository.delete(deletingEmotion);
    }
}
