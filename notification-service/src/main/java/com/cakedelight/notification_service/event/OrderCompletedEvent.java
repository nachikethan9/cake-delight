package com.cakedelight.notification_service.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderCompletedEvent(
        String eventId,
        String eventType,
        int eventVersion,
        Instant occurredAt,
        Long orderId,
        String userId,
        String customerEmail,
        BigDecimal totalAmount,
        List<Item> items
) {
    public static final String TYPE = "ORDER_COMPLETED";

    public record Item(
            Long cakeId,
            String cakeName,
            int quantity,
            BigDecimal unitPrice,
            BigDecimal lineTotal
    ) {
    }
}