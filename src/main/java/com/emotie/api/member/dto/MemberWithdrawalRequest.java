package com.emotie.api.member.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
public class MemberWithdrawalRequest {
    @NotBlank(message = "비밀번호를 입력해주세요.")
    private final String password;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    private final String reason;

    @JsonCreator
    @Builder
    public MemberWithdrawalRequest(@JsonProperty(value = "password", required = true) String password,
                                   @JsonProperty(value = "reason", required = true) String reason) {
        this.password = password;
        this.reason = reason;
    }
}
