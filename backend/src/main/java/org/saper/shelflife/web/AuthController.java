package org.saper.shelflife.web;

import org.saper.shelflife.dto.AuthResponseDto;
import org.saper.shelflife.dto.LoginRequestDto;
import org.saper.shelflife.dto.UserProfileDto;
import org.saper.shelflife.dto.UserRegistrationDto;
import org.saper.shelflife.service.AuthService;
import org.saper.shelflife.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173") // adjust for your frontend
public class AuthController {

    private final UserService userService;
    private final AuthService authService;

    public AuthController(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserProfileDto> register(@RequestBody UserRegistrationDto dto) {
        UserProfileDto profile = userService.registerUser(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(profile);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody LoginRequestDto dto) {
        AuthResponseDto response = authService.login(dto);
        return ResponseEntity.ok(response);
    }
}
