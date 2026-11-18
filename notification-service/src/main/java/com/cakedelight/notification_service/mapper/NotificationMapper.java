package com.cakedelight.notification_service.mapper;

import com.cakedelight.notification_service.dto.NotificationResponse;
import com.cakedelight.notification_service.entity.Notification;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {

    public NotificationResponse toResponse(Notification n) {
        return new NotificationResponse(
                n.getId(), n.getEventId(), n.getOrderId(), n.getUserId(), n.getRecipient(),
                n.getChannel(), n.getStatus(), n.getSubject(), n.getMessage(),
                n.getFailureReason(), n.getCreatedAt(), n.getSentAt());
    }
}