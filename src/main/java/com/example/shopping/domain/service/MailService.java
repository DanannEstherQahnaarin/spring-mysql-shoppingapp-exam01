package com.example.shopping.domain.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender javaMailSender;

    public void sendEmail(String toEmail, String title, String text) {
        // 실제 메일 발송 시도
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject(title);
            message.setText(text);
            javaMailSender.send(message);
        } catch (Exception e) {
            // SMTP 설정이 없거나 실패해도 튜토리얼 진행을 위해 로그만 남기고 넘어감
            log.error("메일 발송 실패 (SMTP 설정을 확인하세요): {}", e.getMessage());
        }
        
        // 개발용: 콘솔에 내용 출력
        log.info("==========================================");
        log.info("To: {}", toEmail);
        log.info("Subject: {}", title);
        log.info("Text: {}", text);
        log.info("==========================================");
    }
}