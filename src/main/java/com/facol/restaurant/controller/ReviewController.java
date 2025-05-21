package com.facol.restaurant.controller;

import com.facol.restaurant.dto.ReviewCreateDto;
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

    @PostMapping("/users/{idUsuario}/restaurants/{idRestaurant}")
    public ResponseEntity<Void> createReview(@RequestBody ReviewCreateDto reviewRequestDto, @PathVariable Long idUsuario, @PathVariable Long idRestaurant) {
        reviewService.createReview(reviewRequestDto, idUsuario, idRestaurant);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateReview(@PathVariable Long id, @RequestBody ReviewRequestDto reviewRequestDto) {
        reviewService.updateReview(id, reviewRequestDto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> patchReview(@PathVariable Long id, @RequestBody ReviewRequestDto reviewRequestDto) {
        reviewService.pathReview(id, reviewRequestDto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteReview(@RequestBody Long id) {
        reviewService.deleteReview(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
