package com.cakedelight.rating_service.service;

import com.cakedelight.rating_service.dto.PagedResponse;
import com.cakedelight.rating_service.dto.RatingRequest;
import com.cakedelight.rating_service.dto.RatingResponse;
import com.cakedelight.rating_service.dto.RatingSummary;
import com.cakedelight.rating_service.entity.Rating;
import com.cakedelight.rating_service.exception.DuplicateRatingException;
import com.cakedelight.rating_service.exception.RatingNotFoundException;
import com.cakedelight.rating_service.mapper.RatingMapper;
import com.cakedelight.rating_service.repository.RatingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class RatingService {

    private final RatingRepository ratingRepository;
    private final RatingMapper ratingMapper;

    @Transactional
    public RatingResponse submitRating(RatingRequest request) {
        String userId = request.userId().trim();
        if (ratingRepository.existsByCakeIdAndUserId(request.cakeId(), userId)) {
            throw new DuplicateRatingException(request.cakeId(), userId);
        }
        Rating saved = ratingRepository.save(ratingMapper.toEntity(request));
        log.info("Rating saved id={} cakeId={} userId={} score={}",
                saved.getId(), saved.getCakeId(), saved.getUserId(), saved.getScore());
        return ratingMapper.toResponse(saved);
    }

    public RatingResponse getRatingById(Long id) {
        return ratingMapper.toResponse(findRatingOrThrow(id));
    }

    public PagedResponse<RatingResponse> getRatingsForCake(Long cakeId, Pageable pageable) {
        return PagedResponse.from(
                ratingRepository.findByCakeId(cakeId, pageable).map(ratingMapper::toResponse));
    }

    public RatingSummary getSummary(Long cakeId) {
        return ratingRepository.findSummaryByCakeId(cakeId)
                .map(s -> new RatingSummary(s.cakeId(), roundToOneDecimal(s.averageRating()), s.ratingCount()))
                .orElse(new RatingSummary(cakeId, 0.0, 0L));
    }

    @Transactional
    public void deleteRating(Long id) {
        if (!ratingRepository.existsById(id)) {
            throw new RatingNotFoundException(id);
        }
        ratingRepository.deleteById(id);
        log.info("Rating deleted id={}", id);
    }

    private Rating findRatingOrThrow(Long id) {
        return ratingRepository.findById(id)
                .orElseThrow(() -> new RatingNotFoundException(id));
    }

    private double roundToOneDecimal(double value) {
        return Math.round(value * 10.0) / 10.0;
    }
}