package org.saper.shelflife.service;

import org.saper.shelflife.dto.UserProfileDto;
import org.saper.shelflife.dto.UserRegistrationDto;
import org.saper.shelflife.model.User;
import org.saper.shelflife.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
@Validated
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ---------- Commands ----------

    public UserProfileDto registerUser(UserRegistrationDto dto) {
        String username = normalizeUsername(dto.username());
        String email = normalizeEmail(dto.email());

        if (username == null || username.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username is required");
        }
        if (email == null || email.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is required");
        }
        if (dto.password() == null || dto.password().length() < 8) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Password must be at least 8 characters"
            );
        }

        // Uniqueness checks
        if (userRepository.existsByUsername(username)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Username already taken"
            );
        }
        if (userRepository.existsByEmail(email)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Email already registered"
            );
        }

        String displayName =
                dto.displayName() != null && !dto.displayName().isBlank()
                        ? dto.displayName().trim()
                        : username;

        User user = User.create(
                username,
                email,
                passwordEncoder.encode(dto.password()),
                displayName
        );

        User saved = userRepository.save(user);
        return toProfileDto(saved);
    }   // <-- this was missing

    // ---------- Queries ----------

    @Transactional(readOnly = true)
    public UserProfileDto getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found"
                ));
        return toProfileDto(user);
    }

    @Transactional(readOnly = true)
    public User findByIdOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found"
                ));
    }

    // ---------- Helpers ----------

    private String normalizeUsername(String username) {
        if (username == null) return null;
        return username.trim().toLowerCase();
    }

    private String normalizeEmail(String email) {
        if (email == null) return null;
        return email.trim().toLowerCase();
    }

    private UserProfileDto toProfileDto(User user) {
        return new UserProfileDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getDisplayName(),
                user.getCreatedAt()
        );
    }
}
