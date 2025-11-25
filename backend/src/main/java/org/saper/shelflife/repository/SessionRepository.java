package org.saper.shelflife.repository;

import org.saper.shelflife.model.Session;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SessionRepository extends JpaRepository<Session, Long> {

    List<Session> findByUserId(Long userId);

    List<Session> findByUserIdAndWorkId(Long userId, Long workId);
}