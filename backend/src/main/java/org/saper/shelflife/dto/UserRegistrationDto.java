package org.saper.shelflife.dto;

public record UserRegistrationDto(
        String username,
        String email,
        String password,
        String displayName
) {
}
