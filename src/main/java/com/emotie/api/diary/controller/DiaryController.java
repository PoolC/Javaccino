package com.emotie.api.diary.controller;

import com.emotie.api.diary.dto.*;
import com.emotie.api.diary.service.DiaryService;
import com.emotie.api.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@Validated
@RequestMapping("/diaries")
@RequiredArgsConstructor
public class DiaryController {
    private static final int PAGE_SIZE = 10;
    private final DiaryService diaryService;

    @PostMapping
    public ResponseEntity<Void> write(
            @AuthenticationPrincipal Member user, @RequestBody @Valid DiaryCreateRequest diaryCreateRequest
    ) {
        diaryService.create(user, diaryCreateRequest);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/{diaryId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DiaryReadResponse> read(
            @AuthenticationPrincipal Member user, @PathVariable Long diaryId
    ) throws Exception {
        return ResponseEntity.ok(diaryService.read(user, diaryId));
    }

    @GetMapping(value = "/user/{memberId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DiaryReadAllResponse> readAll(
            @AuthenticationPrincipal Member user, @PathVariable String memberId,
            @RequestParam("page") @Min(0) @Max(Integer.MAX_VALUE / PAGE_SIZE) Integer page
    ) throws Exception {
        return ResponseEntity.ok(diaryService.readAll(user, memberId, page));
    }

    @DeleteMapping
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal Member user, @RequestBody @Valid DiaryDeleteRequest diaryDeleteRequest
    ) throws Exception {
        diaryService.delete(user, diaryDeleteRequest);
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
