package com.cakedelight.order_service.mapper;

import com.cakedelight.order_service.dto.OrderItemResponse;
import com.cakedelight.order_service.dto.OrderResponse;
import com.cakedelight.order_service.dto.OrderSummaryResponse;
import com.cakedelight.order_service.entity.CustomerOrder;
import com.cakedelight.order_service.entity.OrderItem;
import org.springframework.stereotype.Component;

@Component
public class OrderMapper {

    public OrderResponse toResponse(CustomerOrder order) {
        return new OrderResponse(
                order.getId(),
                order.getUserId(),
                order.getCustomerEmail(),
                order.getStatus(),
                order.getItems().stream().map(this::toItemResponse).toList(),
                order.getTotalAmount(),
                order.getCreatedAt(),
                order.getUpdatedAt()
        );
    }

    public OrderSummaryResponse toSummary(CustomerOrder order) {
        return new OrderSummaryResponse(
                order.getId(),
                order.getStatus(),
                order.getTotalAmount(),
                order.getCreatedAt()
        );
    }

    private OrderItemResponse toItemResponse(OrderItem item) {
        return new OrderItemResponse(
                item.getCakeId(),
                item.getCakeName(),
                item.getUnitPrice(),
                item.getQuantity(),
                item.getLineTotal()
        );
    }
}