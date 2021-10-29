package com.emotie.api.auth.controller;

import com.emotie.api.auth.dto.LoginRequest;
import com.emotie.api.auth.dto.LoginResponse;
import com.emotie.api.auth.dto.PasswordResetRequest;
import com.emotie.api.member.domain.Member;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public interface AuthController {
    public ResponseEntity<LoginResponse> createAccessToken(LoginRequest request);

    public ResponseEntity<Void> sendEmailAuthorizationToken(Member member) throws Exception;

    public ResponseEntity<Void> checkAuthorizationCode(Optional<String> email, Optional<String> authorizationToken);

    public ResponseEntity<Void> sendEmailPasswordResetToken(Optional<String> email) throws Exception;

    public ResponseEntity<Void> updatePassword(Optional<String> passwordResetToken, PasswordResetRequest request);
}
