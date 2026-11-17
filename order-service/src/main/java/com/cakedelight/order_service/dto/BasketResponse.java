package com.cakedelight.order_service.dto;

import java.math.BigDecimal;
import java.util.List;

public record BasketResponse(
        String userId,
        List<BasketItemResponse> items,
        int totalItems,
        BigDecimal totalAmount
) {
}