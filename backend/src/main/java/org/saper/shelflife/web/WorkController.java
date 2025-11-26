package org.saper.shelflife.web;

import jakarta.validation.Valid;
import org.saper.shelflife.dto.WorkCreateUpdateDto;
import org.saper.shelflife.dto.WorkDetailDto;
import org.saper.shelflife.dto.WorkSummaryDto;
import org.saper.shelflife.service.WorkService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/works")
@CrossOrigin(origins = "http://localhost:5173") // adjust for other dev origins if needed
public class WorkController {

    private final WorkService workService;

    public WorkController(WorkService workService) {
        this.workService = workService;
    }

    // TODO: replace this with a SecurityContext lookup when auth is in place
    private Long getCurrentUserId() {
        return 1L;
    }

    @GetMapping
    public ResponseEntity<List<WorkSummaryDto>> getWorks() {
        Long userId = getCurrentUserId();
        List<WorkSummaryDto> works = workService.getWorksForUser(userId);
        return ResponseEntity.ok(works);
    }

    @GetMapping("/{workId}")
    public ResponseEntity<WorkDetailDto> getWork(@PathVariable Long workId) {
        Long userId = getCurrentUserId();
        WorkDetailDto work = workService.getWorkById(userId, workId);
        return ResponseEntity.ok(work);
    }

    @PostMapping
    public ResponseEntity<WorkDetailDto> createWork(
            @Valid @RequestBody WorkCreateUpdateDto dto
    ) {
        Long userId = getCurrentUserId();
        WorkDetailDto created = workService.createWork(userId, dto);
        // For now: 200 OK is fine; later you can switch to 201 Created + Location header.
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{workId}")
    public ResponseEntity<WorkDetailDto> updateWork(
            @PathVariable Long workId,
            @Valid @RequestBody WorkCreateUpdateDto dto
    ) {
        Long userId = getCurrentUserId();
        WorkDetailDto updated = workService.updateWork(userId, workId, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{workId}")
    public ResponseEntity<Void> deleteWork(@PathVariable Long workId) {
        Long userId = getCurrentUserId();
        workService.deleteWork(userId, workId);
        return ResponseEntity.noContent().build();
    }
}
