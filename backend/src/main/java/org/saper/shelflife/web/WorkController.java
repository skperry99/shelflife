package org.saper.shelflife.web;

import jakarta.validation.Valid;
import org.saper.shelflife.dto.WorkCreateUpdateDto;
import org.saper.shelflife.dto.WorkDetailDto;
import org.saper.shelflife.dto.WorkSummaryDto;
import org.saper.shelflife.service.WorkService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/works")
public class WorkController {

    private final WorkService workService;

    public WorkController(WorkService workService) {
        this.workService = workService;
    }

    @GetMapping
    public List<WorkSummaryDto> getWorks(
            @RequestHeader(value = "Authorization", required = false) String authHeader
    ) {
        Long userId = extractUserIdFromDemoToken(authHeader);
        return workService.getWorksForUser(userId);
    }

    @GetMapping("/{workId}")
    public WorkDetailDto getWork(
            @PathVariable Long workId,
            @RequestHeader(value = "Authorization", required = false) String authHeader
    ) {
        Long userId = extractUserIdFromDemoToken(authHeader);
        return workService.getWorkById(userId, workId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WorkDetailDto createWork(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @Valid @RequestBody WorkCreateUpdateDto dto
    ) {
        Long userId = extractUserIdFromDemoToken(authHeader);
        return workService.createWork(userId, dto);
    }

    @PutMapping("/{workId}")
    public WorkDetailDto updateWork(
            @PathVariable Long workId,
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @Valid @RequestBody WorkCreateUpdateDto dto
    ) {
        Long userId = extractUserIdFromDemoToken(authHeader);
        return workService.updateWork(userId, workId, dto);
    }

    @DeleteMapping("/{workId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteWork(
            @PathVariable Long workId,
            @RequestHeader(value = "Authorization", required = false) String authHeader
    ) {
        Long userId = extractUserIdFromDemoToken(authHeader);
        workService.deleteWork(userId, workId);
    }

    // ---------- Demo-token helper (matches AuthController /me) ----------

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
