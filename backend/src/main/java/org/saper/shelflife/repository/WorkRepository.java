package org.saper.shelflife.repository;

import org.saper.shelflife.model.Work;
import org.saper.shelflife.model.WorkStatus;
import org.saper.shelflife.model.WorkType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkRepository extends JpaRepository<Work,Long> {
    List<Work> findByUserId(Long userId);
    List<Work> findByUserIdAndStatus(Long userId, WorkStatus status);

    List<Work> findByUserIdAndType(Long userId, WorkType type);
}
