package org.saper.shelflife.dto;

import jakarta.validation.constraints.*;

public record UserRegistrationDto(
        @NotBlank @Size(min=3, max=50) String username,
        @NotBlank @Email String email,
        @NotBlank @Size(min=8) String password,
        @Size(max=100) String displayName
) {}

