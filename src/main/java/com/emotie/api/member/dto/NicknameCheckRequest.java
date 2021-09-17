package com.emotie.api.member.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
public class NicknameCheckRequest {
    @NotBlank(message = "닉네임을 입력해주세요.")
    @Size(message = "닉네임은 32글자보다 짧고, 적어도 1글자 이상이어야 합니다.", max = 32)
    private final String nickname;

    @JsonCreator
    @Builder
    public NicknameCheckRequest(@JsonProperty(value = "nickname", required = true) String nickname) {
        this.nickname = nickname;
    }
}
