package com.cakedelight.order_service.exception;

public class EmptyBasketException extends RuntimeException {

    public EmptyBasketException(String userId) {
        super("Cannot checkout: the basket of user '" + userId + "' is empty");
    }
}