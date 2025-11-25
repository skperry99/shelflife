package org.saper.shelflife.web;

import org.saper.shelflife.dto.SessionCreateUpdateDto;
import org.saper.shelflife.dto.SessionDto;
import org.saper.shelflife.service.SessionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sessions")
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

    // GET /api/sessions or /api/sessions?workId=1
    @GetMapping
    public ResponseEntity<List<SessionDto>> getSessions(
            @RequestParam(name = "workId", required = false) Long workId
    ) {
        Long userId = getCurrentUserId();
        List<SessionDto> result = (workId == null)
                ? sessionService.getSessionsForUser(userId)
                : sessionService.getSessionsForWork(userId, workId);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SessionDto> getSession(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        SessionDto dto = sessionService.getSession(userId, id);
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<SessionDto> createSession(@RequestBody SessionCreateUpdateDto dto) {
        Long userId = getCurrentUserId();
        SessionDto created = sessionService.createSession(userId, dto);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SessionDto> updateSession(
            @PathVariable Long id,
            @RequestBody SessionCreateUpdateDto dto
    ) {
        Long userId = getCurrentUserId();
        SessionDto updated = sessionService.updateSession(userId, id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSession(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        sessionService.deleteSession(userId, id);
        return ResponseEntity.noContent().build();
    }
}