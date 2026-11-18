package com.cakedelight.notification_service.sender;

import com.cakedelight.notification_service.entity.Notification;

public interface NotificationSender {

    void send(Notification notification);
}