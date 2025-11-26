package org.saper.shelflife.repository;

import org.saper.shelflife.model.Work;
import org.saper.shelflife.model.WorkStatus;
import org.saper.shelflife.model.WorkType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WorkRepository extends JpaRepository<Work, Long> {

    /**
     * All works for a given user (any status / type).
     */
    List<Work> findByUserId(Long userId);

    /**
     * Works for a user filtered by status (TO_EXPLORE, IN_PROGRESS, FINISHED, etc.).
     */
    List<Work> findByUserIdAndStatus(Long userId, WorkStatus status);

    /**
     * Works for a user filtered by type (BOOK, MOVIE, GAME, etc.).
     */
    List<Work> findByUserIdAndType(Long userId, WorkType type);

    /**
     * Fetch a single work that belongs to a given user.
     * Useful for enforcing per-user access at the repository level.
     */
    Optional<Work> findByIdAndUserId(Long id, Long userId);
}
