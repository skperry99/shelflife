package org.saper.shelflife.dto;

import jakarta.validation.constraints.*;
import org.saper.shelflife.model.WorkStatus;
import org.saper.shelflife.model.WorkType;

import java.time.LocalDate;

/**
 * Payload for creating or updating a Work.
 * Used by POST /api/works and PUT /api/works/{id}.
 */
public record WorkCreateUpdateDto(
        @NotBlank
        @Size(max = 255)
        String title,

        @NotNull
        WorkType type,

        @Size(max = 255)
        String creator,

        @Size(max = 100)
        String genre,

        // Allow null; service can default to TO_EXPLORE if you ever want.
        @NotNull
        WorkStatus status,

        // Optional: treat as "pages", "minutes", etc.
        @Min(1)
        @Max(1_000_000)
        Integer totalUnits,

        @Size(max = 500)
        String coverUrl,

        @PastOrPresent
        LocalDate startedAt,

        @PastOrPresent
        LocalDate finishedAt
) {
}
