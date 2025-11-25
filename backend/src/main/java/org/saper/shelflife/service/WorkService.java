package org.saper.shelflife.service;

import org.saper.shelflife.dto.WorkCreateUpdateDto;
import org.saper.shelflife.dto.WorkDetailDto;
import org.saper.shelflife.dto.WorkSummaryDto;
import org.saper.shelflife.model.*;
import org.saper.shelflife.repository.UserRepository;
import org.saper.shelflife.repository.WorkRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public List<WorkSummaryDto> getWorksForUser(Long userId) {
        return workRepository.findByUserId(userId).stream()
                .map(this::toSummaryDto)
                .toList();
    }

    public WorkDetailDto getWorkById(Long userId, Long workId) {
        Work work = workRepository.findById(workId)
                .filter(w -> w.getUser().getId().equals(userId))
                .orElseThrow(() -> new IllegalArgumentException("Work not found"));
        return toDetailDto(work);
    }

    public WorkDetailDto createWork(Long userId, WorkCreateUpdateDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Work work = new Work();
        work.setUser(user);
        applyDtoToWork(dto, work);

        Work saved = workRepository.save(work);
        return toDetailDto(saved);
    }

    public WorkDetailDto updateWork(Long userId, Long workId, WorkCreateUpdateDto dto) {
        Work work = workRepository.findById(workId)
                .filter(w -> w.getUser().getId().equals(userId))
                .orElseThrow(() -> new IllegalArgumentException("Work not found"));

        applyDtoToWork(dto, work);
        Work saved = workRepository.save(work);
        return toDetailDto(saved);
    }

    public void deleteWork(Long userId, Long workId) {
        Work work = workRepository.findById(workId)
                .filter(w -> w.getUser().getId().equals(userId))
                .orElseThrow(() -> new IllegalArgumentException("Work not found"));

        workRepository.delete(work);
    }

    // ----- mapping helpers -----

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
