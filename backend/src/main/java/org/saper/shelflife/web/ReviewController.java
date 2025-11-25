package org.saper.shelflife.web;

import org.saper.shelflife.dto.ReviewCreateUpdateDto;
import org.saper.shelflife.dto.ReviewDto;
import org.saper.shelflife.service.ReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@CrossOrigin(origins = "http://localhost:5173") // adjust as needed
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    // TODO: plug in real auth
    private Long getCurrentUserId() {
        return 1L;
    }

    // GET /api/reviews  -> all reviews for current user
    @GetMapping
    public ResponseEntity<List<ReviewDto>> getReviews() {
        Long userId = getCurrentUserId();
        List<ReviewDto> reviews = reviewService.getReviewsForUser(userId);
        return ResponseEntity.ok(reviews);
    }

    // GET /api/reviews/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ReviewDto> getReview(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        ReviewDto dto = reviewService.getReviewById(userId, id);
        return ResponseEntity.ok(dto);
    }

    // GET /api/reviews/work/{workId} -> current user's review for that work
    @GetMapping("/work/{workId}")
    public ResponseEntity<ReviewDto> getReviewForWork(@PathVariable Long workId) {
        Long userId = getCurrentUserId();
        ReviewDto dto = reviewService.getReviewForWork(userId, workId);
        return ResponseEntity.ok(dto);
    }

    // POST /api/reviews  (upsert by user + work)
    @PostMapping
    public ResponseEntity<ReviewDto> upsertReview(@RequestBody ReviewCreateUpdateDto dto) {
        Long userId = getCurrentUserId();
        ReviewDto saved = reviewService.upsertReview(userId, dto);
        return ResponseEntity.ok(saved);
    }

    // DELETE /api/reviews/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        reviewService.deleteReview(userId, id);
        return ResponseEntity.noContent().build();
    }
}