package com.cakedelight.notification_service.sender;

import com.cakedelight.notification_service.entity.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;


@Component
@Slf4j
@ConditionalOnProperty(name = "app.notification.sender", havingValue = "log")
public class LoggingEmailSender implements NotificationSender {

    @Override
    public void send(Notification notification) {
        log.info("""
                
                ===================== EMAIL (simulated) =====================
                To:      {}
                Subject: {}
                -------------------------------------------------------------
                {}
                =============================================================""",
                notification.getRecipient(), notification.getSubject(), notification.getMessage());
    }
}