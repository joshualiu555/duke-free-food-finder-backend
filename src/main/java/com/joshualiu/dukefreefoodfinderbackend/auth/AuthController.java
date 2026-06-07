package com.joshualiu.dukefreefoodfinderbackend.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/send-code")
    public ResponseEntity<Void> sendCode(@RequestBody Map<String, String> body) {
        authService.sendCode(body.get("email"));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/verify-code")
    public ResponseEntity<Map<String, String>> verifyCode(@RequestBody Map<String, String> body) {
        String token = authService.verifyCode(body.get("email"), body.get("code"));
        return ResponseEntity.ok(Map.of("token", token));
    }
}
