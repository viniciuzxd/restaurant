package com.facol.restaurant.controller;

import com.facol.restaurant.dto.ReviewRequestDto;
import com.facol.restaurant.dto.ReviewResponseDto;
import com.facol.restaurant.service.ReviewService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping
    public ResponseEntity<Page<ReviewResponseDto>> getAllReviews() {
        return ResponseEntity.ok(reviewService.getReviews());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReviewResponseDto> getReviewById(@PathVariable Long id) {
        return ResponseEntity.ok(reviewService.getReviewById(id));
    }

    @GetMapping("/restaurants/{id}")
    public ResponseEntity<Page<ReviewResponseDto>> getReviewsByRestaurantId(@PathVariable Long id) {
        return ResponseEntity.ok(reviewService.getReviewsByRestaurantId(id));
    }

    @PostMapping
    public ResponseEntity<Void> createReview(@RequestBody ReviewRequestDto reviewRequestDto) {
        reviewService.createReview(reviewRequestDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteReview(@RequestBody ReviewRequestDto reviewRequestDto, long id) {
        reviewService.deleteReview(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
