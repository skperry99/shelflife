package org.saper.shelflife.web;

import jakarta.validation.Valid;
import org.saper.shelflife.dto.AuthResponseDto;
import org.saper.shelflife.dto.LoginRequestDto;
import org.saper.shelflife.dto.UserProfileDto;
import org.saper.shelflife.dto.UserRegistrationDto;
import org.saper.shelflife.service.AuthService;
import org.saper.shelflife.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final AuthService authService;

    public AuthController(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserProfileDto> register(@Valid @RequestBody UserRegistrationDto dto) {
        UserProfileDto profile = userService.registerUser(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(profile);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody LoginRequestDto dto) {
        AuthResponseDto response = authService.login(dto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public UserProfileDto me(
            @RequestHeader(value = "Authorization", required = false) String authHeader
    ) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Missing or invalid Authorization header"
            );
        }

        String token = authHeader.substring("Bearer ".length());
        String prefix = "demo-token-user-";

        if (!token.startsWith(prefix)) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid token"
            );
        }

        Long userId;
        try {
            userId = Long.parseLong(token.substring(prefix.length()));
        } catch (NumberFormatException ex) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid token"
            );
        }

        return userService.getUserProfile(userId);
    }
}
