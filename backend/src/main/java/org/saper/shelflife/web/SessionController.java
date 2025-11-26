package org.saper.shelflife.web;

import org.saper.shelflife.dto.SessionCreateUpdateDto;
import org.saper.shelflife.dto.SessionDto;
import org.saper.shelflife.service.SessionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173") // adjust for your frontend
public class SessionController {

    private final SessionService sessionService;

    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    // TODO: replace with real auth
    private Long getCurrentUserId() {
        return 1L;
    }

    // ---------- Collection endpoints ----------

    // GET /api/sessions or /api/sessions?workId=1
    @GetMapping("/sessions")
    public ResponseEntity<List<SessionDto>> getSessions(
            @RequestParam(name = "workId", required = false) Long workId
    ) {
        Long userId = getCurrentUserId();
        List<SessionDto> result = (workId == null)
                ? sessionService.getSessionsForUser(userId)
                : sessionService.getSessionsForWork(userId, workId);

        return ResponseEntity.ok(result);
    }

    // GET /api/sessions/{id}
    @GetMapping("/sessions/{id}")
    public ResponseEntity<SessionDto> getSession(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        SessionDto dto = sessionService.getSession(userId, id);
        return ResponseEntity.ok(dto);
    }

    // POST /api/sessions
    @PostMapping("/sessions")
    public ResponseEntity<SessionDto> createSession(@RequestBody SessionCreateUpdateDto dto) {
        Long userId = getCurrentUserId();
        SessionDto created = sessionService.createSession(userId, dto);
        return ResponseEntity.ok(created);
    }

    // PUT /api/sessions/{id}
    @PutMapping("/sessions/{id}")
    public ResponseEntity<SessionDto> updateSession(
            @PathVariable Long id,
            @RequestBody SessionCreateUpdateDto dto
    ) {
        Long userId = getCurrentUserId();
        SessionDto updated = sessionService.updateSession(userId, id, dto);
        return ResponseEntity.ok(updated);
    }

    // DELETE /api/sessions/{id}
    @DeleteMapping("/sessions/{id}")
    public ResponseEntity<Void> deleteSession(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        sessionService.deleteSession(userId, id);
        return ResponseEntity.noContent().build();
    }

    // ---------- Work-scoped endpoints (match frontend) ----------

    /**
     * GET /api/works/{workId}/sessions
     * <p>
     * This is what your WorkDetailPage calls via getWorkSessions(workId).
     */
    @GetMapping("/works/{workId}/sessions")
    public ResponseEntity<List<SessionDto>> getSessionsForWork(@PathVariable Long workId) {
        Long userId = getCurrentUserId();
        List<SessionDto> sessions = sessionService.getSessionsForWork(userId, workId);
        return ResponseEntity.ok(sessions);
    }

    /**
     * POST /api/works/{workId}/sessions
     * <p>
     * Optional: nice future endpoint for "Log session" directly from a work detail page.
     */
    @PostMapping("/works/{workId}/sessions")
    public ResponseEntity<SessionDto> createSessionForWork(
            @PathVariable Long workId,
            @RequestBody SessionCreateUpdateDto body
    ) {
        Long userId = getCurrentUserId();

        SessionCreateUpdateDto dto = new SessionCreateUpdateDto(
                workId,
                body.startedAt(),
                body.endedAt(),
                body.minutes(),
                body.unitsCompleted(),
                body.note()
        );

        SessionDto created = sessionService.createSessionForWork(userId, workId, dto);
        return ResponseEntity.ok(created);
    }
}
