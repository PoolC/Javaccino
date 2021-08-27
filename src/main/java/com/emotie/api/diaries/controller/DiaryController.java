package com.emotie.api.diaries.controller;

import com.emotie.api.diaries.dto.DiaryCreateRequest;
import com.emotie.api.diaries.service.DiaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/diaries")
@RequiredArgsConstructor
public class DiaryController {
    private final DiaryService diaryService;

    @PostMapping
    public ResponseEntity<Void> write(@RequestBody @Valid DiaryCreateRequest diaryCreateRequest) throws Exception{
        diaryService.create(diaryCreateRequest);
        return ResponseEntity.ok().build();
    }
}
