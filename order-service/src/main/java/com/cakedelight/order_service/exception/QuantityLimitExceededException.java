package com.cakedelight.order_service.exception;

public class QuantityLimitExceededException extends RuntimeException {

    public QuantityLimitExceededException(int requested, int max) {
        super("Quantity " + requested + " exceeds the maximum of " + max + " per cake");
    }
}