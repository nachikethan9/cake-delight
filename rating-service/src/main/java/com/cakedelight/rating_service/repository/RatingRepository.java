package com.cakedelight.rating_service.repository;

import com.cakedelight.rating_service.dto.RatingSummary;
import com.cakedelight.rating_service.entity.Rating;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {

    boolean existsByCakeIdAndUserId(Long cakeId, String userId);

    Page<Rating> findByCakeId(Long cakeId, Pageable pageable);

    @Query("""
            SELECT new com.cakedelight.rating_service.dto.RatingSummary(
                       r.cakeId, AVG(r.score), COUNT(r))
            FROM Rating r
            WHERE r.cakeId = :cakeId
            GROUP BY r.cakeId
            """)
    Optional<RatingSummary> findSummaryByCakeId(@Param("cakeId") Long cakeId);
}