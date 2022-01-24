package com.emotie.api.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Component
@RequiredArgsConstructor
public class JavaMailServiceImpl implements MailService {
    private final JavaMailSender mailSender;
    private String address = System.getenv("EMOTIE_DOMAIN");

    @Override
    public void sendEmailAuthorizationToken(String email, String authorizationToken) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");
        helper.setSubject("[Emotie] Emotie 회원가입 인증 안내 메일입니다.");
        helper.setText(String.format("안녕하세요. Emotie입니다. 회원 인증을 완료하시려면 아래의 링크를 클릭해 주세요.\n" +
                        "(%s) \n\n 이용해 주셔서 감사합니다." +
                        "- Emotie 올림 - \n\n\n\n\n\n\n\n (인증을 위한 링크는 24시간 동안만 유효합니다)",
                address + "/auth/authorization?email=" + email + "&authorizationToken=" + authorizationToken));
        helper.setTo(email);
        mailSender.send(message);
    }

    @Override
    public void sendEmailPasswordResetToken(String email, String resetPasswordToken) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");
        helper.setSubject("[Emotie] Emotie 비밀번호 재설정 안내 메일입니다.");
        helper.setText(String.format("안녕하세요 Emotie입니다. 비밀번호 재설정을 완료하시려면 아래의 링크를 클릭해 주세요.\n" +
                        "(%s) \n\n 이용해 주셔서 감사합니다." +
                        "- Emotie 올림 - \n\n\n\n\n\n\n\n (인증을 위한 링크는 24시간 동안만 유효합니다)",
                address + "/auth/password-reset?email=" + email + "&passwordResetToken="+resetPasswordToken));
        helper.setTo(email);
        mailSender.send(message);
    }
}