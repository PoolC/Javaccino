package com.emotie.api.auth.controller;

import com.emotie.api.auth.dto.LoginRequest;
import com.emotie.api.auth.dto.LoginResponse;
import com.emotie.api.auth.dto.PasswordResetRequest;
import com.emotie.api.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LoginResponse> createAccessToken(@RequestBody LoginRequest request) {
        String accessToken = authService.createAccessToken(request.getEmail(), request.getPassword());
        return ResponseEntity.ok(new LoginResponse(accessToken));
    }

    @PostMapping(value = "/authorization")
    public ResponseEntity<Void> sendEmailAuthorizationToken(@RequestParam Optional<String> email) throws Exception {
        authService.sendEmailAuthorizationToken(email);
        return ResponseEntity.ok().build();
    }

    @PutMapping(value = "/authorization")
    public ResponseEntity<Void> checkAuthorizationCode(@RequestParam Optional<String> email,
                                                       @RequestParam(name = "AuthorizationToken") Optional<String> authorizationToken) {
        authService.checkAuthorizationTokenRequestAndChangeMemberRole(email, authorizationToken);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/password-reset")
    public ResponseEntity<Void> sendEmailPasswordResetToken(@RequestParam(name = "Email") Optional<String> email) throws Exception {
        authService.sendEmailPasswordResetToken(email);
        return ResponseEntity.ok().build();
    }

    @PutMapping(value = "/password-reset")
    public ResponseEntity<Void> updatePassword(@RequestParam(name = "PasswordResetToken") Optional<String> passwordResetToken,
                                               @RequestBody PasswordResetRequest request) {
        authService.checkPasswordResetRequestAndUpdatePassword(passwordResetToken, request);
        return ResponseEntity.ok().build();
    }
}
