package com.cakedelight.rating_service.controller;

import com.cakedelight.rating_service.dto.PagedResponse;
import com.cakedelight.rating_service.dto.RatingRequest;
import com.cakedelight.rating_service.dto.RatingResponse;
import com.cakedelight.rating_service.dto.RatingSummary;
import com.cakedelight.rating_service.service.RatingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/ratings")
@RequiredArgsConstructor
@Tag(name = "Ratings", description = "Submit and view cake ratings")
public class RatingController {

    private final RatingService ratingService;

    @Operation(summary = "Submit a rating for a cake (one rating per user per cake)")
    @PostMapping
    public ResponseEntity<RatingResponse> submitRating(@Valid @RequestBody RatingRequest request) {
        RatingResponse created = ratingService.submitRating(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @Operation(summary = "Get a rating by its ID")
    @GetMapping("/{id}")
    public ResponseEntity<RatingResponse> getRatingById(@PathVariable Long id) {
        return ResponseEntity.ok(ratingService.getRatingById(id));
    }

    @Operation(summary = "List ratings for a cake (newest first by default)")
    @GetMapping("/cake/{cakeId}")
    public ResponseEntity<PagedResponse<RatingResponse>> getRatingsForCake(
            @PathVariable Long cakeId,
            @ParameterObject
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(ratingService.getRatingsForCake(cakeId, pageable));
    }

    @Operation(summary = "Get the average rating and rating count for a cake")
    @GetMapping("/cake/{cakeId}/summary")
    public ResponseEntity<RatingSummary> getSummary(@PathVariable Long cakeId) {
        return ResponseEntity.ok(ratingService.getSummary(cakeId));
    }

    @Operation(summary = "Delete a rating")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRating(@PathVariable Long id) {
        ratingService.deleteRating(id);
        return ResponseEntity.noContent().build();
    }
}