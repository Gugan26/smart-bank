package com.example.smartbank.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.smartbank.model.User;
import com.example.smartbank.service.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody Map<String, String> body) {
        try {
            String name = body.get("name");
            String email = body.get("email");
            String password = body.get("password");

            User u = authService.register(name, email, password);
            return Map.of(
                "id", u.getId(),
                "name", u.getName(),
                "email", u.getEmail()
            );
        } catch (Exception e) {
            return Map.of("error", e.getMessage());
        }
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> body) {
        try {
            String email = body.get("email");
            String password = body.get("password");

            User u = authService.login(email, password);
            return Map.of(
                "id", u.getId(),
                "name", u.getName(),
                "email", u.getEmail()
            );
        } catch (Exception e) {
            return Map.of("error", e.getMessage());
        }
    }
}
