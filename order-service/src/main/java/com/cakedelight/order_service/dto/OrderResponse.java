package com.cakedelight.order_service.dto;

import com.cakedelight.order_service.entity.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        Long orderId,
        String userId,
        String customerEmail,
        OrderStatus status,
        List<OrderItemResponse> items,
        BigDecimal totalAmount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}