package com.cakedelight.notification_service.sender;

import com.cakedelight.notification_service.entity.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "app.notification.sender", havingValue = "smtp")
public class SmtpEmailSender implements NotificationSender {

    private final JavaMailSender mailSender;

    @Value("${app.notification.from}")
    private String from;

    @Override
    public void send(Notification notification) {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setFrom(from);
        mail.setTo(notification.getRecipient());
        mail.setSubject(notification.getSubject());
        mail.setText(notification.getMessage());

        mailSender.send(mail);
        log.info("Email sent via SMTP to={} subject={}", notification.getRecipient(), notification.getSubject());
    }
}