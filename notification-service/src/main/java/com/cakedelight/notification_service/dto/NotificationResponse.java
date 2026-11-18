package com.cakedelight.notification_service.dto;

import com.cakedelight.notification_service.entity.NotificationChannel;
import com.cakedelight.notification_service.entity.NotificationStatus;

import java.time.LocalDateTime;

public record NotificationResponse(
        Long id,
        String eventId,
        Long orderId,
        String userId,
        String recipient,
        NotificationChannel channel,
        NotificationStatus status,
        String subject,
        String message,
        String failureReason,
        LocalDateTime createdAt,
        LocalDateTime sentAt
) {
}