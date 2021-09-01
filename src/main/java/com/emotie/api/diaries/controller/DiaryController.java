package com.emotie.api.diaries.controller;

import com.emotie.api.diaries.dto.DiaryCreateRequest;
import com.emotie.api.diaries.dto.DiaryExportRequest;
import com.emotie.api.diaries.dto.DiaryReadResponse;
import com.emotie.api.diaries.service.DiaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping(value = "/follow/{nickname}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DiaryReadResponse> read() throws Exception {
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/export")
    public ResponseEntity<Void> export(@RequestBody @Valid DiaryExportRequest diaryExportRequest) throws Exception {
        return ResponseEntity.ok().build();
    }

}
