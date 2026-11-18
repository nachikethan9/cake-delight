package com.cakedelight.order_service.event;

import com.cakedelight.order_service.entity.CustomerOrder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

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
    public static final int VERSION = 1;

    public record Item(
            Long cakeId,
            String cakeName,
            int quantity,
            BigDecimal unitPrice,
            BigDecimal lineTotal
    ) {
    }

    public static OrderCompletedEvent from(CustomerOrder order) {
        List<Item> items = order.getItems().stream()
                .map(i -> new Item(i.getCakeId(), i.getCakeName(), i.getQuantity(),
                        i.getUnitPrice(), i.getLineTotal()))
                .toList();

        return new OrderCompletedEvent(
                UUID.randomUUID().toString(),
                TYPE,
                VERSION,
                Instant.now(),
                order.getId(),
                order.getUserId(),
                order.getCustomerEmail(),
                order.getTotalAmount(),
                items
        );
    }
}