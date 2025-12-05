package org.saper.shelflife.web;

import jakarta.validation.Valid;
import org.saper.shelflife.dto.ReviewCreateUpdateDto;
import org.saper.shelflife.dto.ReviewDto;
import org.saper.shelflife.service.ReviewService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    // ---------- Review collection endpoints ----------

    // GET /api/reviews  -> all reviews for current user
    @GetMapping("/reviews")
    public List<ReviewDto> getReviews(
            @RequestHeader(value = "Authorization", required = false) String authHeader
    ) {
        Long userId = extractUserIdFromDemoToken(authHeader);
        return reviewService.getReviewsForUser(userId);
    }

    // GET /api/reviews/{id} -> single review by id (must belong to current user)
    @GetMapping("/reviews/{id}")
    public ReviewDto getReview(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String authHeader
    ) {
        Long userId = extractUserIdFromDemoToken(authHeader);
        return reviewService.getReviewById(userId, id);
    }

    // DELETE /api/reviews/{id}
    @DeleteMapping("/reviews/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteReview(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String authHeader
    ) {
        Long userId = extractUserIdFromDemoToken(authHeader);
        reviewService.deleteReview(userId, id);
    }

    // ---------- Work-scoped endpoints (match frontend) ----------

    /**
     * GET /api/works/{workId}/review
     * Returns either a ReviewDto or null if the user hasn't reviewed this work yet.
     */
    @GetMapping("/works/{workId}/review")
    public ReviewDto getReviewForWork(
            @PathVariable Long workId,
            @RequestHeader(value = "Authorization", required = false) String authHeader
    ) {
        Long userId = extractUserIdFromDemoToken(authHeader);
        return reviewService.getReviewForWorkOrNull(userId, workId); // may be null; 200 with null body is OK
    }

    /**
     * PUT /api/works/{workId}/review
     * Upserts the current user's review for this work.
     */
    @PutMapping("/works/{workId}/review")
    public ReviewDto upsertReviewForWork(
            @PathVariable Long workId,
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @Valid @RequestBody ReviewCreateUpdateDto body
    ) {
        Long userId = extractUserIdFromDemoToken(authHeader);

        // Ensure path and body workId can't drift apart
        ReviewCreateUpdateDto dto = new ReviewCreateUpdateDto(
                workId,
                body.rating(),
                body.title(),
                body.body(),
                body.isPrivate()
        );

        return reviewService.upsertReview(userId, dto);
    }

    // ---------- Demo-token helper (same pattern as other controllers) ----------

    private Long extractUserIdFromDemoToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Missing or invalid Authorization header"
            );
        }

        String token = authHeader.substring("Bearer ".length());
        String prefix = "demo-token-user-";

        if (!token.startsWith(prefix)) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid token"
            );
        }

        try {
            return Long.parseLong(token.substring(prefix.length()));
        } catch (NumberFormatException ex) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid token"
            );
        }
    }
}
