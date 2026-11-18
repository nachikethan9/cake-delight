package com.cakedelight.notification_service.sender;

import com.cakedelight.notification_service.entity.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
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