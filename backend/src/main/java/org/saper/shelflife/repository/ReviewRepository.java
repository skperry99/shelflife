package org.saper.shelflife.repository;

import org.saper.shelflife.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review,Long> {
    Optional<Review> findByUserIdAndWorkId(Long userId, Long workId);
    List<Review> findByUserId(Long userId);
}
