package com.example.movieticket.controller;

import com.example.movieticket.model.User;
import com.example.movieticket.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) {
        User savedUser = authService.register(user);
        return ResponseEntity.ok(savedUser);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody User user) {
        Optional<User> loggedIn = authService.login(user.getUsername(), user.getPassword());
        if (loggedIn.isPresent()) {
            return ResponseEntity.ok("Login successful for user: " + loggedIn.get().getUsername());
        }
        return ResponseEntity.status(401).body("Invalid username or password");
    }
}
