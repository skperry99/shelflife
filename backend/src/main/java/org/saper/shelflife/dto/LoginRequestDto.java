package org.saper.shelflife.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequestDto(
        @NotBlank String usernameOrEmail,
        @NotBlank String password
) {}
