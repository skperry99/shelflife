package org.saper.shelflife.service;

import org.saper.shelflife.dto.AuthResponseDto;
import org.saper.shelflife.dto.LoginRequestDto;
import org.saper.shelflife.dto.UserProfileDto;
import org.saper.shelflife.model.User;
import org.saper.shelflife.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            UserService userService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
    }

    public AuthResponseDto login(LoginRequestDto dto) {
        String identifier = dto.usernameOrEmail() != null
                ? dto.usernameOrEmail().trim()
                : "";

        if (identifier.isBlank() || dto.password() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Username/email and password are required"
            );
        }

        User user;
        if (identifier.contains("@")) {
            // Treat as email
            String email = identifier.toLowerCase();
            user = userRepository.findByEmail(email)
                    .orElseThrow(() -> invalidCredentials());
        } else {
            // Treat as username
            String username = identifier.toLowerCase();
            user = userRepository.findByUsername(username)
                    .orElseThrow(() -> invalidCredentials());
        }

        if (!passwordEncoder.matches(dto.password(), user.getPasswordHash())) {
            throw invalidCredentials();
        }

        UserProfileDto profile = userService.getUserProfile(user.getId());

        // TODO: replace with real JWT / session token
        String demoToken = "demo-token-user-" + user.getId();

        return new AuthResponseDto(demoToken, profile);
    }

    private ResponseStatusException invalidCredentials() {
        return new ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "Invalid username/email or password"
        );
    }
}
