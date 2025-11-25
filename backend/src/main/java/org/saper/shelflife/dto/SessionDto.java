package org.saper.shelflife.dto;

import java.time.Instant;

public record SessionDto(
        Long id,
        Long workId,
        Instant startedAt,
        Instant endedAt,
        Integer minutes,
        Integer unitsCompleted,
        String note
) {}