package com.emotie.api.diary.controller;

import com.emotie.api.diary.domain.Diary;
import com.emotie.api.emotion.domain.Emotion;
import com.emotie.api.diary.dto.*;
import com.emotie.api.diary.service.DiaryService;
import com.emotie.api.emotion.repository.EmotionRepository;
import com.emotie.api.emotion.service.EmotionService;
import com.emotie.api.member.domain.Member;
import com.emotie.api.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/diaries")
@RequiredArgsConstructor
public class DiaryController {
    private final DiaryService diaryService;
    private final MemberService memberService;

    @PostMapping
    public ResponseEntity<Void> write(
            @AuthenticationPrincipal Member user, @RequestBody @Valid DiaryCreateRequest diaryCreateRequest
    ) throws Exception{
        diaryService.create(user, diaryCreateRequest);
        memberService.deepenEmotionScore(user, diaryCreateRequest.getEmotion());
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/{diaryId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DiaryReadResponse> read(
            @AuthenticationPrincipal Member user, @PathVariable Integer diaryId
    ) throws Exception {
        Diary diary = diaryService.read(user, diaryId);
        return ResponseEntity.ok(new DiaryReadResponse(diary));
    }

    @GetMapping(value = "/user/{nickname}/page/{pageNumber}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DiaryReadAllResponse> readAll(
            @PathVariable String nickname, @PathVariable Integer pageNumber
    ) throws Exception {
        return ResponseEntity.ok().build();
    }

    @PutMapping(value = "/{diaryId}")
    public ResponseEntity<Void> update(
            @AuthenticationPrincipal Member user, @PathVariable Integer diaryId,
            @RequestBody @Valid DiaryUpdateRequest diaryUpdateRequest
    ) throws Exception {
        String originalEmotion = diaryService.update(user, diaryId, diaryUpdateRequest);
        String updatingEmotion = diaryUpdateRequest.getEmotion();
        memberService.updateEmotionScore(user, originalEmotion, updatingEmotion);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal Member user, @RequestBody @Valid DiaryDeleteRequest diaryDeleteRequest
    ) throws Exception{
        List<Diary> diaries = diaryService.delete(user, diaryDeleteRequest);
        diaries.forEach(
                (diary) -> {
                    memberService.reduceEmotionScore(user, diary.getEmotion().getEmotion());
                }
        );
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/export")
    public ResponseEntity<Void> export(
            @AuthenticationPrincipal Member user, @RequestBody @Valid DiaryExportRequest diaryExportRequest
    ) throws Exception {
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/export_all")
    public ResponseEntity<Void> export_all(
            @RequestBody @Valid DiaryExportAllRequest diaryExportAllRequest
    ) throws Exception {
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/report/{diaryId}")
    public ResponseEntity<DiaryReportResponse> report(@PathVariable Integer diaryId) throws Exception {
        return ResponseEntity.ok().build();
    }
}
