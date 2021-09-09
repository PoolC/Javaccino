package com.emotie.api.diaries.service;

import com.emotie.api.auth.exception.UnauthorizedException;
import com.emotie.api.diaries.domain.Diaries;
import com.emotie.api.diaries.dto.DiaryCreateRequest;
import com.emotie.api.diaries.repository.DiariesRepository;
import com.emotie.api.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

// TODO: 2021-08-06
@SuppressWarnings("unused")
@Service
@RequiredArgsConstructor
public class DiaryService {
    private final DiariesRepository diariesRepository;

    public void create(Member user, DiaryCreateRequest diaryCreateRequest) {
        checkCreateRequestValidity(user, diaryCreateRequest);
        diariesRepository.save(
                Diaries.builder()
                        .writerId(user.getUUID())
                        .emotion(diaryCreateRequest.getEmotion())
                        .content(diaryCreateRequest.getContent())
                        .isOpened(diaryCreateRequest.getIsOpened())
                        .build()
        );
    }

    private void checkCreateRequestValidity(Member user, DiaryCreateRequest diaryCreateRequest) {
        if (!user.getRoles().isAcceptedMember()) throw new UnauthorizedException("인증 된 회원만 이용할 수 있는 서비스입니다.");
    }
}
