package com.example.movieticket.controller;

import com.example.movieticket.model.User;
import com.example.movieticket.service.AuthService;
import com.example.movieticket.security.JwtUtil;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    // Constructor injection (preferred)
    public AuthController(AuthService authService, JwtUtil jwtUtil) {
        this.authService = authService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        User savedUser = authService.register(user);
        // hide password before returning
        savedUser.setPassword(null);
        return ResponseEntity.ok(savedUser);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        Optional<User> loggedIn = authService.login(user.getUsername(), user.getPassword());
        if (loggedIn.isPresent()) {
            User u = loggedIn.get();
            // generate JWT token (stores username, role, userId)
            String token = jwtUtil.generateToken(u.getUsername(), u.getRole(), u.getId());
            // hide password
            u.setPassword(null);
            // return both user and token
            return ResponseEntity.ok(Map.of("user", u, "token", token));
        }
        return ResponseEntity.status(401).body(Map.of("error", "Invalid username or password"));
    }
}
