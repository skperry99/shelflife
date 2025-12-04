package org.saper.shelflife.dto;

import java.time.Instant;

public record UserProfileDto(
        Long id,
        String username,
        String email,
        String displayName,
        Instant createdAt
) {
}
