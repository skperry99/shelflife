package org.saper.shelflife.web;

import org.saper.shelflife.service.WorkService;
import org.saper.shelflife.dto.WorkCreateUpdateDto;
import org.saper.shelflife.dto.WorkDetailDto;
import org.saper.shelflife.dto.WorkSummaryDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/works")
@CrossOrigin(origins = "http://localhost:5173") // adjust to your React dev origin
public class WorkController {

    private final WorkService workService;

    public WorkController(WorkService workService) {
        this.workService = workService;
    }

    // TODO: replace this with SecurityContext lookup when auth is in place
    private Long getCurrentUserId() {
        return 1L;
    }

    @GetMapping
    public ResponseEntity<List<WorkSummaryDto>> getWorks() {
        Long userId = getCurrentUserId();
        List<WorkSummaryDto> works = workService.getWorksForUser(userId);
        return ResponseEntity.ok(works);
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkDetailDto> getWork(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        WorkDetailDto work = workService.getWorkById(userId, id);
        return ResponseEntity.ok(work);
    }

    @PostMapping
    public ResponseEntity<WorkDetailDto> createWork(@RequestBody WorkCreateUpdateDto dto) {
        Long userId = getCurrentUserId();
        WorkDetailDto created = workService.createWork(userId, dto);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<WorkDetailDto> updateWork(
            @PathVariable Long id,
            @RequestBody WorkCreateUpdateDto dto
    ) {
        Long userId = getCurrentUserId();
        WorkDetailDto updated = workService.updateWork(userId, id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWork(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        workService.deleteWork(userId, id);
        return ResponseEntity.noContent().build();
    }
}
