package com.cakedelight.order_service.exception;

import com.cakedelight.order_service.entity.OrderStatus;

public class InvalidStatusTransitionException extends RuntimeException {

    public InvalidStatusTransitionException(Long orderId, OrderStatus from, OrderStatus to) {
        super("Order " + orderId + " cannot move from " + from + " to " + to);
    }
}