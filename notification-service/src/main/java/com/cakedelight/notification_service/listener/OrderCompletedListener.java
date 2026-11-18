package com.cakedelight.notification_service.listener;

import com.cakedelight.notification_service.event.OrderCompletedEvent;
import com.cakedelight.notification_service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderCompletedListener {

    private final JsonMapper jsonMapper;
    private final NotificationService notificationService;

    @KafkaListener(topics = "${app.kafka.topics.order-completed}")
    public void onOrderCompleted(ConsumerRecord<String, String> record) {
        log.info("Received message key={} partition={} offset={}",
                record.key(), record.partition(), record.offset());

        OrderCompletedEvent event;
        try {
            event = jsonMapper.readValue(record.value(), OrderCompletedEvent.class);
        } catch (JacksonException ex) {
            log.error("Skipping malformed message partition={} offset={}: {}",
                    record.partition(), record.offset(), ex.getOriginalMessage());
            return;
        }

        if (!OrderCompletedEvent.TYPE.equals(event.eventType())) {
            log.warn("Ignoring unexpected eventType={} at offset={}", event.eventType(), record.offset());
            return;
        }

        notificationService.handleOrderCompleted(event);
    }
}