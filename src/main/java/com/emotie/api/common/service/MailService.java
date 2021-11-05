package com.emotie.api.common.service;

import javax.mail.MessagingException;

public interface MailService {
    public void sendEmailAuthorizationToken(String email, String authorizationToken) throws MessagingException;

    public void sendEmailPasswordResetToken(String email, String resetPasswordToken) throws MessagingException;
}
