package com.emotie.api.auth.service;

import com.emotie.api.auth.dto.PasswordResetRequest;
import com.emotie.api.member.domain.Member;

import javax.mail.MessagingException;
import java.util.Optional;

public interface AuthService {
    public String createAccessToken(String loginId, String password);
    
    public void checkAuthorizationTokenRequestAndChangeMemberRole(Member loginMember, Optional<String> AuthorizationToken);

    public void sendEmailPasswordResetToken(Optional<String> email) throws MessagingException;

    public void checkPasswordResetRequestAndUpdatePassword(Optional<String> passwordResetToken, PasswordResetRequest request);
}
