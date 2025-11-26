package org.saper.shelflife.dto;

import org.saper.shelflife.model.WorkStatus;
import org.saper.shelflife.model.WorkType;

/**
 * Lightweight summary used for library shelves / list views.
 */
public record WorkSummaryDto(
        Long id,
        String title,
        String creator,
        WorkType type,
        String genre,
        WorkStatus status
) {
}
