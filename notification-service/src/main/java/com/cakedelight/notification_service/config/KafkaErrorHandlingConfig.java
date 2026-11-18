package com.cakedelight.notification_service.config;

import com.cakedelight.notification_service.event.OrderCompletedEvent;
import com.cakedelight.notification_service.exception.MalformedEventException;
import com.cakedelight.notification_service.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.ExponentialBackOffWithMaxRetries;
import tools.jackson.databind.json.JsonMapper;

@Configuration
@Slf4j
public class KafkaErrorHandlingConfig {

    @Bean
    public NewTopic orderCompletedDltTopic(@Value("${app.kafka.topics.order-completed-dlt}") String dltTopic) {
        return TopicBuilder.name(dltTopic).partitions(3).replicas(1).build();
    }

    @Bean
    public DefaultErrorHandler kafkaErrorHandler(KafkaTemplate<String, String> kafkaTemplate,
                                                 NotificationService notificationService,
                                                 JsonMapper jsonMapper,
                                                 @Value("${app.kafka.topics.order-completed-dlt}") String dltTopic) {

        // 1. Where failed messages go: the DLT, same partition number as the original
        DeadLetterPublishingRecoverer dltPublisher = new DeadLetterPublishingRecoverer(kafkaTemplate,
                (record, ex) -> new TopicPartition(dltTopic, record.partition()));

        // 2. What happens after the LAST retry fails: record FAILED status, then send to DLT
        DefaultErrorHandler handler = new DefaultErrorHandler((record, ex) -> {
            String reason = NestedExceptionUtils.getMostSpecificCause(ex).getMessage();
            log.error("Giving up on message partition={} offset={} - sending to {}. Reason: {}",
                    record.partition(), record.offset(), dltTopic, reason);
            try {
                OrderCompletedEvent event = jsonMapper.readValue((String) record.value(), OrderCompletedEvent.class);
                notificationService.recordFailure(event, reason);
            } catch (Exception parseEx) {
                log.warn("Could not record failure status (message not parseable)");
            }
            dltPublisher.accept(record, ex);
        }, backOff());

        // 3. Never retry messages that can never succeed
        handler.addNotRetryableExceptions(MalformedEventException.class);
        return handler;
    }

    private ExponentialBackOffWithMaxRetries backOff() {
        ExponentialBackOffWithMaxRetries backOff = new ExponentialBackOffWithMaxRetries(3);
        backOff.setInitialInterval(1_000L);   // 1s
        backOff.setMultiplier(2.0);           // then 2s, 4s
        backOff.setMaxInterval(10_000L);
        return backOff;
    }
}