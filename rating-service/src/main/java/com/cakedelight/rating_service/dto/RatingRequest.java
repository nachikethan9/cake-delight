package com.cakedelight.rating_service.dto;

import jakarta.validation.constraints.*;

public record RatingRequest(

        @NotNull(message = "Cake ID is required")
        @Positive(message = "Cake ID must be a positive number")
        Long cakeId,

        @NotBlank(message = "User ID is required")
        @Size(max = 100, message = "User ID must be at most 100 characters")
        String userId,

        @NotNull(message = "Score is required")
        @Min(value = 1, message = "Score must be between 1 and 5")
        @Max(value = 5, message = "Score must be between 1 and 5")
        Integer score,

        @Size(max = 500, message = "Comment must be at most 500 characters")
        String comment
) {
}