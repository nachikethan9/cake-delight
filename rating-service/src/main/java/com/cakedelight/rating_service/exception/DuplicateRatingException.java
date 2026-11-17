package com.cakedelight.rating_service.exception;

public class DuplicateRatingException extends RuntimeException {

    public DuplicateRatingException(Long cakeId, String userId) {
        super("User '" + userId + "' has already rated cake " + cakeId);
    }
}