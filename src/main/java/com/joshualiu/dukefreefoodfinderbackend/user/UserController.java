package com.joshualiu.dukefreefoodfinderbackend.user;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getById(@PathVariable Long id) {
        User user = service.getUserById(id);
        String email = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();
        if (!user.getEmail().equals(email)) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(user);
    }
}
