package com.cakedelight.order_service.exception;

public class BasketItemNotFoundException extends RuntimeException {

    public BasketItemNotFoundException(String userId, Long cakeId) {
        super("Cake " + cakeId + " is not in the basket of user '" + userId + "'");
    }
}