package org.saper.shelflife.dto;

import org.saper.shelflife.model.WorkStatus;
import org.saper.shelflife.model.WorkType;

import java.time.LocalDate;

public record WorkCreateUpdateDto(
        String title,
        WorkType type,
        String creator,
        String genre,
        WorkStatus status,
        Integer totalUnits,
        String coverUrl,
        LocalDate startedAt,
        LocalDate finishedAt
) {}
