package com.emotie.api.emotion.controller;

import com.emotie.api.common.exception.DuplicatedException;
import com.emotie.api.emotion.dto.EmotionCreateRequest;
import com.emotie.api.emotion.dto.EmotionResponseUnused;
import com.emotie.api.emotion.dto.EmotionUpdateRequest;
import com.emotie.api.emotion.dto.EmotionsResponse;
import com.emotie.api.emotion.repository.EmotionRepository;
import com.emotie.api.emotion.service.EmotionService;
import com.emotie.api.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/emotions")
@RequiredArgsConstructor
public class EmotionController {
    private final EmotionService emotionService;
    private final EmotionRepository emotionRepository;

    @GetMapping()
    public ResponseEntity<EmotionsResponse> getAllEmotions(){
        List<EmotionResponseUnused> emotions = emotionService.getAllEmotions().stream()
                .map(EmotionResponseUnused::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(new EmotionsResponse(emotions));
    }

    @PostMapping()
    public ResponseEntity<Void> createEmotion(@AuthenticationPrincipal Member member, @RequestBody @Valid EmotionCreateRequest request){
        checkDuplicateEmotion(request.getEmotion());
        checkDuplicateColor(request.getColor());
        emotionService.createEmotion(request);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{emotionId}")
    public ResponseEntity<Void> updateReview(@AuthenticationPrincipal Member member, @RequestBody @Valid EmotionUpdateRequest request, @PathVariable("emotionId") Integer emotionId){
        checkEmotionIdExist(emotionId);
        emotionService.updateEmotion(request, emotionId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{emotionId}")
    public ResponseEntity<Void> deleteEmotion(@AuthenticationPrincipal Member member,@PathVariable("emotionId") Integer emotionId){
        checkEmotionIdExist(emotionId);
        emotionService.deleteEmotion(emotionId);
        return ResponseEntity.ok().build();
    }
    public void checkEmotionIdExist(Integer emotionId){
        emotionRepository.findById(emotionId).orElseThrow(()-> new NoSuchElementException("잘못된 emotionId 입니다."));
    }

    public void checkDuplicateEmotion(String emotion){
        if(emotionRepository.findByEmotion(emotion).isPresent()){
            throw new DuplicatedException("중복된 감정입니다.");
        }
    }

    public void checkDuplicateColor(String color){
        if(emotionRepository.findByEmotion(color).isPresent()){
            throw new DuplicatedException("중복된 색상입니다.");
        }
    }
}
