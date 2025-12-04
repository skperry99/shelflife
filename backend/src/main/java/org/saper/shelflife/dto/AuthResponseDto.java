package org.saper.shelflife.dto;

public record AuthResponseDto(
        String token,
        UserProfileDto user
) {
}
