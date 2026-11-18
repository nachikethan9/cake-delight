package com.cakedelight.notification_service.service;

import com.cakedelight.notification_service.dto.NotificationResponse;
import com.cakedelight.notification_service.dto.PagedResponse;
import com.cakedelight.notification_service.entity.Notification;
import com.cakedelight.notification_service.entity.NotificationChannel;
import com.cakedelight.notification_service.entity.NotificationStatus;
import com.cakedelight.notification_service.event.OrderCompletedEvent;
import com.cakedelight.notification_service.exception.NotificationDeliveryException;
import com.cakedelight.notification_service.mapper.NotificationMapper;
import com.cakedelight.notification_service.repository.NotificationRepository;
import com.cakedelight.notification_service.sender.NotificationSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationSender notificationSender;
    private final NotificationMapper notificationMapper;

    @Transactional
    public void handleOrderCompleted(OrderCompletedEvent event) {
        // 1. Idempotency: the same event may arrive more than once
        if (notificationRepository.existsByEventId(event.eventId())) {
            log.info("Duplicate event ignored eventId={} orderId={}", event.eventId(), event.orderId());
            return;
        }

        // 2. Build and save
        Notification notification = newNotification(event);
        notificationRepository.save(notification);

        // 3. Send. On failure THROW -> transaction rolls back -> Kafka error handler retries
        try {
            notificationSender.send(notification);
        } catch (Exception ex) {
            throw new NotificationDeliveryException(
                    "Could not deliver notification for order " + event.orderId(), ex);
        }

        notification.markSent();
        log.info("Notification SENT orderId={} to={}", event.orderId(), event.customerEmail());
    }

    /** Called only after all retries are exhausted. */
    @Transactional
    public void recordFailure(OrderCompletedEvent event, String reason) {
        if (notificationRepository.existsByEventId(event.eventId())) {
            return;
        }
        Notification notification = newNotification(event);
        notification.markFailed(reason);
        notificationRepository.save(notification);
        log.warn("Notification recorded as FAILED orderId={} reason={}", event.orderId(), reason);
    }

    public PagedResponse<NotificationResponse> getNotificationsForUser(String userId, Pageable pageable) {
        return PagedResponse.from(
                notificationRepository.findByUserId(userId.trim(), pageable).map(notificationMapper::toResponse));
    }

    public List<NotificationResponse> getNotificationsForOrder(Long orderId) {
        return notificationRepository.findByOrderIdOrderByCreatedAtDesc(orderId).stream()
                .map(notificationMapper::toResponse)
                .toList();
    }

    private Notification newNotification(OrderCompletedEvent event) {
        Notification notification = new Notification();
        notification.setEventId(event.eventId());
        notification.setOrderId(event.orderId());
        notification.setUserId(event.userId());
        notification.setRecipient(event.customerEmail());
        notification.setChannel(NotificationChannel.EMAIL);
        notification.setStatus(NotificationStatus.PENDING);
        notification.setSubject("Cake Delight - Order #" + event.orderId() + " confirmed");
        notification.setMessage(buildMessage(event));
        return notification;
    }

    private String buildMessage(OrderCompletedEvent event) {
        StringBuilder sb = new StringBuilder();
        sb.append("Hi ").append(event.userId()).append(",\n\n");
        sb.append("Thank you for your order! Order #").append(event.orderId()).append(" is confirmed.\n\n");
        for (OrderCompletedEvent.Item item : event.items()) {
            sb.append(String.format("  %-25s x%-3d %10s%n", item.cakeName(), item.quantity(), item.lineTotal()));
        }
        sb.append(String.format("%n  %-30s %10s%n", "TOTAL", event.totalAmount()));
        sb.append("\nWe'll let you know when your cakes are on the way.\n- Cake Delight");
        return sb.toString();
    }
}