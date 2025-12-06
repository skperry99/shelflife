package org.saper.shelflife.web;

import org.saper.shelflife.dto.UserProfileDto;
import org.saper.shelflife.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public UserProfileDto me(
            @RequestHeader(value = "Authorization", required = false) String authHeader
    ) {
        Long userId = extractUserIdFromDemoToken(authHeader);
        return userService.getUserProfile(userId);
    }

    // ---------- Demo-token helper ----------

    private Long extractUserIdFromDemoToken(String authHeader) {
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

        try {
            return Long.parseLong(token.substring(prefix.length()));
        } catch (NumberFormatException ex) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid token"
            );
        }
    }
}
