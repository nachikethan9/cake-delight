package com.cakedelight.order_service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import tools.jackson.databind.json.JsonMapper;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final JsonMapper jsonMapper;

    @Value("${app.kafka.topics.order-completed}")
    private String orderCompletedTopic;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onOrderCompleted(OrderCompletedEvent event) {
        String key = String.valueOf(event.orderId());
        String payload = jsonMapper.writeValueAsString(event);

        try {
            kafkaTemplate.send(orderCompletedTopic, key, payload)
                    .whenComplete((result, ex) -> {
                        if (ex != null) {
                            log.error("Failed to publish {} for orderId={} eventId={}",
                                    event.eventType(), event.orderId(), event.eventId(), ex);
                        } else {
                            log.info("Published {} orderId={} eventId={} partition={} offset={}",
                                    event.eventType(), event.orderId(), event.eventId(),
                                    result.getRecordMetadata().partition(),
                                    result.getRecordMetadata().offset());
                        }
                    });
        } catch (Exception ex) {
            log.error("Could not send {} for orderId={} - Kafka unavailable?",
                    event.eventType(), event.orderId(), ex);
        }
    }
}