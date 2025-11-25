package org.saper.shelflife.service;

import org.saper.shelflife.dto.ReviewCreateUpdateDto;
import org.saper.shelflife.dto.ReviewDto;
import org.saper.shelflife.model.Review;
import org.saper.shelflife.model.User;
import org.saper.shelflife.model.Work;
import org.saper.shelflife.repository.ReviewRepository;
import org.saper.shelflife.repository.UserRepository;
import org.saper.shelflife.repository.WorkRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public List<ReviewDto> getReviewsForUser(Long userId) {
        return reviewRepository.findByUserId(userId).stream()
                .map(this::toDto)
                .toList();
    }

    public ReviewDto getReviewById(Long userId, Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .filter(r -> r.getUser().getId().equals(userId))
                .orElseThrow(() -> new IllegalArgumentException("Review not found"));
        return toDto(review);
    }

    public ReviewDto getReviewForWork(Long userId, Long workId) {
        Review review = reviewRepository.findByUserIdAndWorkId(userId, workId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found for work"));
        return toDto(review);
    }

    public ReviewDto upsertReview(Long userId, ReviewCreateUpdateDto dto) {
        if (dto.rating() == null || dto.rating() < 1 || dto.rating() > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Work work = workRepository.findById(dto.workId())
                .filter(w -> w.getUser().getId().equals(userId))
                .orElseThrow(() -> new IllegalArgumentException("Work not found for user"));

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
                .orElseThrow(() -> new IllegalArgumentException("Review not found"));
        reviewRepository.delete(review);
    }

    // ----- helpers -----

    private ReviewDto toDto(Review r) {
        return new ReviewDto(
                r.getId(),
                r.getWork().getId(),
                r.getRating(),
                r.getTitle(),
                r.getBody(),
                r.isPrivateReview(),
                r.getCreatedAt(),
                r.getUpdatedAt()
        );
    }

    private void applyDto(ReviewCreateUpdateDto dto, Review r) {
        r.setRating(dto.rating());
        r.setTitle(dto.title());
        r.setBody(dto.body());
        r.setPrivateReview(dto.privateReview());
    }
}
