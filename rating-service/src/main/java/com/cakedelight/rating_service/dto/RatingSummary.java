package com.cakedelight.rating_service.dto;

public record RatingSummary(
        Long cakeId,
        Double averageRating,
        Long ratingCount
) {
}