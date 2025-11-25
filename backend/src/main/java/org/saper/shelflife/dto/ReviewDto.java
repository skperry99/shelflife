package org.saper.shelflife.dto;

import java.time.Instant;

public record ReviewDto(
        Long id,
        Long workId,
        Integer rating,
        String title,
        String body,
        boolean privateReview,
        Instant createdAt,
        Instant updatedAt
) {}