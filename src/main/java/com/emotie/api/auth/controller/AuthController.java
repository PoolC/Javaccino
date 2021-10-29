package com.emotie.api.auth.controller;

import com.emotie.api.auth.dto.LoginRequest;
import com.emotie.api.auth.dto.LoginResponse;
import com.emotie.api.auth.dto.PasswordResetRequest;
import com.emotie.api.member.domain.Member;
import org.springframework.http.ResponseEntity;

import javax.mail.MessagingException;
import java.util.Optional;

public interface AuthController {
    public ResponseEntity<LoginResponse> createAccessToken(LoginRequest request);

    public ResponseEntity<Void> checkAuthorizationCode(Member member, Optional<String> authorizationToken);

    public ResponseEntity<Void> sendEmailPasswordResetToken(Optional<String> email) throws MessagingException;

    public ResponseEntity<Void> updatePassword(Optional<String> passwordResetToken, PasswordResetRequest request);
}
