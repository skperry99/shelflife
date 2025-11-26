package org.saper.shelflife.service;

import org.saper.shelflife.dto.ReviewCreateUpdateDto;
import org.saper.shelflife.dto.ReviewDto;
import org.saper.shelflife.model.Review;
import org.saper.shelflife.model.User;
import org.saper.shelflife.model.Work;
import org.saper.shelflife.repository.ReviewRepository;
import org.saper.shelflife.repository.UserRepository;
import org.saper.shelflife.repository.WorkRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final WorkRepository workRepository;

    public ReviewService(ReviewRepository reviewRepository,
                         UserRepository userRepository,
                         WorkRepository workRepository) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.workRepository = workRepository;
    }

    // ---------- Queries ----------

    @Transactional(readOnly = true)
    public List<ReviewDto> getReviewsForUser(Long userId) {
        return reviewRepository.findByUserId(userId).stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public ReviewDto getReviewById(Long userId, Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .filter(r -> r.getUser().getId().equals(userId))
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Review not found"
                ));

        return toDto(review);
    }

    /**
     * Get the review for a given work, or {@code null} if none exists.
     * This is ideal for your /api/works/{workId}/review endpoint, since the
     * frontend can cleanly treat "no review yet" as null rather than an error.
     */
    @Transactional(readOnly = true)
    public ReviewDto getReviewForWorkOrNull(Long userId, Long workId) {
        return reviewRepository.findByUserIdAndWorkId(userId, workId)
                .map(this::toDto)
                .orElse(null);
    }

    // ---------- Commands ----------

    public ReviewDto upsertReview(Long userId, ReviewCreateUpdateDto dto) {
        // Simple rating guard; you can also put Bean Validation on the DTO.
        Integer rating = dto.rating();
        if (rating == null || rating < 1 || rating > 5) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Rating must be between 1 and 5"
            );
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found"
                ));

        Work work = workRepository.findById(dto.workId())
                .filter(w -> w.getUser().getId().equals(userId))
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Work not found for user"
                ));

        Review review = reviewRepository.findByUserIdAndWorkId(userId, dto.workId())
                .orElseGet(Review::new);

        review.setUser(user);
        review.setWork(work);
        applyDto(dto, review);

        Review saved = reviewRepository.save(review);
        return toDto(saved);
    }

    public void deleteReview(Long userId, Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .filter(r -> r.getUser().getId().equals(userId))
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Review not found"
                ));

        reviewRepository.delete(review);
    }

    // ---------- helpers ----------

    private ReviewDto toDto(Review r) {
        return new ReviewDto(
                r.getId(),
                r.getWork().getId(),
                r.getRating(),
                r.getTitle(),
                r.getBody(),
                r.isPrivateReview(), // maps to isPrivate in ReviewDto
                r.getCreatedAt(),
                r.getUpdatedAt()
        );
    }

    private void applyDto(ReviewCreateUpdateDto dto, Review r) {
        r.setRating(dto.rating());
        r.setTitle(dto.title());
        r.setBody(dto.body());
        r.setPrivateReview(dto.isPrivate());
    }
}
