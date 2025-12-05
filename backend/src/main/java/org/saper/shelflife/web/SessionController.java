package org.saper.shelflife.web;

import jakarta.validation.Valid;
import org.saper.shelflife.dto.SessionCreateUpdateDto;
import org.saper.shelflife.dto.SessionDto;
import org.saper.shelflife.service.SessionService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api")
public class SessionController {

    private final SessionService sessionService;

    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    // ---------- Collection endpoints ----------

    // GET /api/sessions or /api/sessions?workId=1
    @GetMapping("/sessions")
    public List<SessionDto> getSessions(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestParam(name = "workId", required = false) Long workId
    ) {
        Long userId = extractUserIdFromDemoToken(authHeader);
        return (workId == null)
                ? sessionService.getSessionsForUser(userId)
                : sessionService.getSessionsForWork(userId, workId);
    }

    // GET /api/sessions/{id}
    @GetMapping("/sessions/{id}")
    public SessionDto getSession(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String authHeader
    ) {
        Long userId = extractUserIdFromDemoToken(authHeader);
        return sessionService.getSession(userId, id);
    }

    // POST /api/sessions
    @PostMapping("/sessions")
    @ResponseStatus(HttpStatus.CREATED)
    public SessionDto createSession(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @Valid @RequestBody SessionCreateUpdateDto dto
    ) {
        Long userId = extractUserIdFromDemoToken(authHeader);
        return sessionService.createSession(userId, dto);
    }

    // PUT /api/sessions/{id}
    @PutMapping("/sessions/{id}")
    public SessionDto updateSession(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @Valid @RequestBody SessionCreateUpdateDto dto
    ) {
        Long userId = extractUserIdFromDemoToken(authHeader);
        return sessionService.updateSession(userId, id, dto);
    }

    // DELETE /api/sessions/{id}
    @DeleteMapping("/sessions/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSession(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String authHeader
    ) {
        Long userId = extractUserIdFromDemoToken(authHeader);
        sessionService.deleteSession(userId, id);
    }

    // ---------- Work-scoped endpoints (match frontend) ----------

    /**
     * GET /api/works/{workId}/sessions
     * Called by WorkDetailPage via getWorkSessions(workId).
     */
    @GetMapping("/works/{workId}/sessions")
    public List<SessionDto> getSessionsForWork(
            @PathVariable Long workId,
            @RequestHeader(value = "Authorization", required = false) String authHeader
    ) {
        Long userId = extractUserIdFromDemoToken(authHeader);
        return sessionService.getSessionsForWork(userId, workId);
    }

    /**
     * POST /api/works/{workId}/sessions
     * "Log session" directly from a work detail page.
     */
    @PostMapping("/works/{workId}/sessions")
    @ResponseStatus(HttpStatus.CREATED)
    public SessionDto createSessionForWork(
            @PathVariable Long workId,
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @Valid @RequestBody SessionCreateUpdateDto body
    ) {
        Long userId = extractUserIdFromDemoToken(authHeader);

        SessionCreateUpdateDto dto = new SessionCreateUpdateDto(
                workId,
                body.startedAt(),
                body.endedAt(),
                body.minutes(),
                body.unitsCompleted(),
                body.note()
        );

        return sessionService.createSessionForWork(userId, workId, dto);
    }

    // ---------- Demo-token helper (same pattern as WorkController/AuthController) ----------

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
