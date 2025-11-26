package org.saper.shelflife.service;

import org.saper.shelflife.dto.WorkCreateUpdateDto;
import org.saper.shelflife.dto.WorkDetailDto;
import org.saper.shelflife.dto.WorkSummaryDto;
import org.saper.shelflife.model.User;
import org.saper.shelflife.model.Work;
import org.saper.shelflife.model.WorkStatus;
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
public class WorkService {

    private final WorkRepository workRepository;
    private final UserRepository userRepository;

    public WorkService(WorkRepository workRepository, UserRepository userRepository) {
        this.workRepository = workRepository;
        this.userRepository = userRepository;
    }

    // ---------- Queries ----------

    @Transactional(readOnly = true)
    public List<WorkSummaryDto> getWorksForUser(Long userId) {
        return workRepository.findByUserId(userId).stream()
                // Sort by *explicit* status order, then by title (case-insensitive)
                .sorted(
                        Comparator
                                .comparingInt((Work w) -> statusSortOrder(w.getStatus()))
                                .thenComparing(
                                        w -> w.getTitle() != null ? w.getTitle() : "",
                                        String.CASE_INSENSITIVE_ORDER
                                )
                )
                .map(this::toSummaryDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public WorkDetailDto getWorkById(Long userId, Long workId) {
        Work work = getWorkForUserOrThrow(userId, workId);
        return toDetailDto(work);
    }

    // ---------- Commands ----------

    public WorkDetailDto createWork(Long userId, WorkCreateUpdateDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found"
                ));

        Work work = new Work();
        work.setUser(user);
        applyDtoToWork(dto, work);

        Work saved = workRepository.save(work);
        return toDetailDto(saved);
    }

    public WorkDetailDto updateWork(Long userId, Long workId, WorkCreateUpdateDto dto) {
        Work work = getWorkForUserOrThrow(userId, workId);
        applyDtoToWork(dto, work);
        Work saved = workRepository.save(work);
        return toDetailDto(saved);
    }

    public void deleteWork(Long userId, Long workId) {
        Work work = getWorkForUserOrThrow(userId, workId);
        workRepository.delete(work);
    }

    // ---------- Internal helpers ----------

    private Work getWorkForUserOrThrow(Long userId, Long workId) {
        // Uses the scoped repository method instead of findById().filter(...)
        return workRepository.findByIdAndUserId(workId, userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Work not found"
                ));
    }

    /**
     * Explicit sort order for statuses so we’re not tied to enum ordinal().
     * Adjust here if you ever add more statuses or want a different sequence.
     */
    private int statusSortOrder(WorkStatus status) {
        if (status == null) return Integer.MAX_VALUE;

        return switch (status) {
            case TO_EXPLORE -> 0;
            case IN_PROGRESS -> 1;
            case FINISHED -> 2;
            // Any future statuses fall to the bottom until you place them explicitly
            default -> 99;
        };
    }

    private WorkSummaryDto toSummaryDto(Work work) {
        return new WorkSummaryDto(
                work.getId(),
                work.getTitle(),
                work.getCreator(),
                work.getType(),
                work.getGenre(),
                work.getStatus()
        );
    }

    private WorkDetailDto toDetailDto(Work work) {
        return new WorkDetailDto(
                work.getId(),
                work.getTitle(),
                work.getType(),
                work.getCreator(),
                work.getGenre(),
                work.getStatus(),
                work.getTotalUnits(),
                work.getCoverUrl(),
                work.getStartedAt(),
                work.getFinishedAt()
        );
    }

    private void applyDtoToWork(WorkCreateUpdateDto dto, Work work) {
        work.setTitle(dto.title());
        work.setType(dto.type());
        work.setCreator(dto.creator());
        work.setGenre(dto.genre());
        work.setStatus(dto.status());
        work.setTotalUnits(dto.totalUnits());
        work.setCoverUrl(dto.coverUrl());
        work.setStartedAt(dto.startedAt());
        work.setFinishedAt(dto.finishedAt());
    }
}
