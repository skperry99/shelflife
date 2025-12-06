package org.saper.shelflife.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public record SessionCreateUpdateDto(
        @NotNull
        Long workId,

        Instant startedAt,
        Instant endedAt,

        @Min(1)
        @Max(1_000_000)
        Integer minutes,

        @Min(0)
        @Max(1_000_000)
        Integer unitsCompleted,

        @Size(max = 500)
        String note
) {
}
