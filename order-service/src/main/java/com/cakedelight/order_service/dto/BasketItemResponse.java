package com.cakedelight.order_service.dto;

import java.math.BigDecimal;

public record BasketItemResponse(
        Long cakeId,
        String cakeName,
        BigDecimal unitPrice,
        int quantity,
        BigDecimal lineTotal
) {
}