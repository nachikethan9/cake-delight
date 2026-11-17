package com.cakedelight.order_service.dto;

import com.cakedelight.order_service.entity.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderSummaryResponse(
        Long orderId,
        OrderStatus status,
        BigDecimal totalAmount,
        LocalDateTime createdAt
) {
}