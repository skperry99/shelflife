// backend/src/main/java/org/saper/shelflife/web/ReviewController.java
package org.saper.shelflife.web;

import org.saper.shelflife.dto.ReviewCreateUpdateDto;
import org.saper.shelflife.dto.ReviewDto;
import org.saper.shelflife.service.ReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
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

    // ---------- Review collection endpoints ----------

    // GET /api/reviews  -> all reviews for current user
    @GetMapping("/reviews")
    public ResponseEntity<List<ReviewDto>> getReviews() {
        Long userId = getCurrentUserId();
        List<ReviewDto> reviews = reviewService.getReviewsForUser(userId);
        return ResponseEntity.ok(reviews);
    }

    // GET /api/reviews/{id} -> single review by id (must belong to current user)
    @GetMapping("/reviews/{id}")
    public ResponseEntity<ReviewDto> getReview(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        ReviewDto dto = reviewService.getReviewById(userId, id);
        return ResponseEntity.ok(dto);
    }

    // DELETE /api/reviews/{id}
    @DeleteMapping("/reviews/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        reviewService.deleteReview(userId, id);
        return ResponseEntity.noContent().build();
    }

    // ---------- Work-scoped endpoints (match frontend) ----------

    /**
     * GET /api/works/{workId}/review
     * <p>
     * Returns either a ReviewDto or null if the user hasn't reviewed this work yet.
     * Your frontend already handles "no review yet" gracefully.
     */
    @GetMapping("/works/{workId}/review")
    public ResponseEntity<ReviewDto> getReviewForWork(@PathVariable Long workId) {
        Long userId = getCurrentUserId();
        ReviewDto dto = reviewService.getReviewForWorkOrNull(userId, workId);
        return ResponseEntity.ok(dto); // 200 with null is fine
    }

    /**
     * PUT /api/works/{workId}/review
     * <p>
     * Upserts the current user's review for this work. Great for a future "Save review" form.
     */
    @PutMapping("/works/{workId}/review")
    public ResponseEntity<ReviewDto> upsertReviewForWork(
            @PathVariable Long workId,
            @RequestBody ReviewCreateUpdateDto body
    ) {
        Long userId = getCurrentUserId();

        // Ensure path and body workId can't drift apart
        ReviewCreateUpdateDto dto = new ReviewCreateUpdateDto(
                workId,
                body.rating(),
                body.title(),
                body.body(),
                body.isPrivate()
        );

        ReviewDto saved = reviewService.upsertReview(userId, dto);
        return ResponseEntity.ok(saved);
    }
}
