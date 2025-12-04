package org.saper.shelflife.dto;

public record LoginRequestDto(
        String usernameOrEmail,
        String password
) {
}
