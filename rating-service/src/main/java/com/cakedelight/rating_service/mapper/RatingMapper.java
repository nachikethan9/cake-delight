package com.cakedelight.rating_service.mapper;

import com.cakedelight.rating_service.dto.RatingRequest;
import com.cakedelight.rating_service.dto.RatingResponse;
import com.cakedelight.rating_service.entity.Rating;
import org.springframework.stereotype.Component;

@Component
public class RatingMapper {

    public Rating toEntity(RatingRequest request) {
        return Rating.builder()
                .cakeId(request.cakeId())
                .userId(request.userId().trim())
                .score(request.score())
                .comment(request.comment())
                .build();
    }

    public RatingResponse toResponse(Rating rating) {
        return new RatingResponse(
                rating.getId(),
                rating.getCakeId(),
                rating.getUserId(),
                rating.getScore(),
                rating.getComment(),
                rating.getCreatedAt()
        );
    }
}