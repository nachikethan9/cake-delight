package com.cakedelight.order_service.dto;

import com.cakedelight.order_service.entity.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateStatusRequest(
        @NotNull(message = "Status is required")
        OrderStatus status
) {
}