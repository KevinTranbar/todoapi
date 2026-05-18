package com.example.todoapi.controller;

import com.example.todoapi.dto.AuthResponse;
import com.example.todoapi.dto.LoginRequest;
import com.example.todoapi.dto.RefreshRequest;
import com.example.todoapi.dto.RegisterRequest;
import com.example.todoapi.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register( //ResponseEntity = Full HTTP response, HttpStatus = HTTP status code (200, 400, etc.)
            @Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.register(request));
    }

    @PostMapping("/login") //Why post instead of get? = With get you send data in URL, including password (not good)
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(userService.login(request)); //.ok = 200 request successful
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(
            @Valid @RequestBody RefreshRequest request) {
        return ResponseEntity.ok(userService.refresh(request.getRefreshToken())); //Need token to validate, then revoke old and create new
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @Valid @RequestBody RefreshRequest request) {
        userService.logout(request.getRefreshToken()); //Need token to ensure real Refresh token
        return ResponseEntity.noContent().build(); //Builds empty response (204)
    }
}
