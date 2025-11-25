package org.saper.shelflife.dto;

public record ReviewCreateUpdateDto(
        Long workId,
        Integer rating,
        String title,
        String body,
        boolean privateReview
) {}