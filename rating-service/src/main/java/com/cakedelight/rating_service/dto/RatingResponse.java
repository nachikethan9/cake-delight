package com.cakedelight.rating_service.dto;

import java.time.LocalDateTime;

public record RatingResponse(
        Long id,
        Long cakeId,
        String userId,
        int score,
        String comment,
        LocalDateTime createdAt
) {
}