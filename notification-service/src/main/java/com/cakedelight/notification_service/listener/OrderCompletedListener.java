package com.cakedelight.notification_service.listener;

import com.cakedelight.notification_service.event.OrderCompletedEvent;
import com.cakedelight.notification_service.exception.MalformedEventException;
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

        OrderCompletedEvent event = parse(record);

        if (!OrderCompletedEvent.TYPE.equals(event.eventType())) {
            log.warn("Ignoring unexpected eventType={} at offset={}", event.eventType(), record.offset());
            return;
        }

        notificationService.handleOrderCompleted(event);
    }

    private OrderCompletedEvent parse(ConsumerRecord<String, String> record) {
        try {
            return jsonMapper.readValue(record.value(), OrderCompletedEvent.class);
        } catch (JacksonException ex) {
            throw new MalformedEventException(
                    "Malformed event at partition=" + record.partition() + " offset=" + record.offset(), ex);
        }
    }
}