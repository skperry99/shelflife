package org.saper.shelflife.service;

import org.saper.shelflife.dto.SessionCreateUpdateDto;
import org.saper.shelflife.dto.SessionDto;
import org.saper.shelflife.model.Session;
import org.saper.shelflife.model.User;
import org.saper.shelflife.model.Work;
import org.saper.shelflife.repository.SessionRepository;
import org.saper.shelflife.repository.UserRepository;
import org.saper.shelflife.repository.WorkRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.List;

@Service
@Transactional
public class SessionService {

    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;
    private final WorkRepository workRepository;

    public SessionService(SessionRepository sessionRepository,
                          UserRepository userRepository,
                          WorkRepository workRepository) {
        this.sessionRepository = sessionRepository;
        this.userRepository = userRepository;
        this.workRepository = workRepository;
    }

    // ---------- Queries ----------

    @Transactional(readOnly = true)
    public List<SessionDto> getSessionsForUser(Long userId) {
        return sessionRepository.findByUserId(userId).stream()
                .sorted(byStartedAtDesc())
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SessionDto> getSessionsForWork(Long userId, Long workId) {
        // Ensure the work belongs to the user
        Work work = findUserWork(userId, workId);
        return sessionRepository.findByUserIdAndWorkId(userId, work.getId()).stream()
                .sorted(byStartedAtDesc())
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public SessionDto getSession(Long userId, Long sessionId) {
        Session session = sessionRepository.findById(sessionId)
                .filter(s -> s.getUser().getId().equals(userId))
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Session not found"
                ));
        return toDto(session);
    }

    // ---------- Commands ----------

    public SessionDto createSession(Long userId, SessionCreateUpdateDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found"
                ));

        Work work = findUserWork(userId, dto.workId());

        Session session = new Session();
        session.setUser(user);
        session.setWork(work);
        applyDto(dto, session);

        Session saved = sessionRepository.save(session);
        return toDto(saved);
    }

    public SessionDto createSessionForWork(Long userId, Long workId, SessionCreateUpdateDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found"
                ));

        Work work = findUserWork(userId, workId);

        Session session = new Session();
        session.setUser(user);
        session.setWork(work);

        SessionCreateUpdateDto merged = new SessionCreateUpdateDto(
                workId,
                dto.startedAt(),
                dto.endedAt(),
                dto.minutes(),
                dto.unitsCompleted(),
                dto.note()
        );

        applyDto(merged, session);

        Session saved = sessionRepository.save(session);
        return toDto(saved);
    }

    public SessionDto updateSession(Long userId, Long sessionId, SessionCreateUpdateDto dto) {
        Session session = sessionRepository.findById(sessionId)
                .filter(s -> s.getUser().getId().equals(userId))
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Session not found"
                ));

        // Optional: allow changing workId, but still enforce ownership
        if (dto.workId() != null && !dto.workId().equals(session.getWork().getId())) {
            Work work = findUserWork(userId, dto.workId());
            session.setWork(work);
        }

        applyDto(dto, session);
        Session saved = sessionRepository.save(session);
        return toDto(saved);
    }

    public void deleteSession(Long userId, Long sessionId) {
        Session session = sessionRepository.findById(sessionId)
                .filter(s -> s.getUser().getId().equals(userId))
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Session not found"
                ));
        sessionRepository.delete(session);
    }

    // ---------- Helpers ----------

    private Work findUserWork(Long userId, Long workId) {
        return workRepository.findById(workId)
                .filter(w -> w.getUser().getId().equals(userId))
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Work not found for user"
                ));
    }

    private SessionDto toDto(Session s) {
        return new SessionDto(
                s.getId(),
                s.getWork().getId(),
                s.getStartedAt(),
                s.getEndedAt(),
                s.getMinutes(),
                s.getUnitsCompleted(),
                s.getNote()
        );
    }

    private void applyDto(SessionCreateUpdateDto dto, Session s) {
        s.setStartedAt(dto.startedAt());
        s.setEndedAt(dto.endedAt());
        s.setMinutes(dto.minutes());
        s.setUnitsCompleted(dto.unitsCompleted());
        s.setNote(dto.note());
    }

    private Comparator<Session> byStartedAtDesc() {
        return Comparator.comparing(
                Session::getStartedAt,
                Comparator.nullsLast(Comparator.naturalOrder())
        ).reversed();
    }
}
