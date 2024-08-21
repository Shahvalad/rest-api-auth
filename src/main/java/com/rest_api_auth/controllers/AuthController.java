package com.rest_api_auth.controllers;

import com.rest_api_auth.models.dtos.AuthDto;
import com.rest_api_auth.models.dtos.UserRegistrationDto;
import com.rest_api_auth.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate user", description = "Authenticate user with provided credentials")
    public ResponseEntity<String> authenticateUser(@RequestBody AuthDto authDto) {
        return authService.login(authDto);
    }

    @PostMapping("/register")
    @Operation(summary = "Register user", description = "Register user with provided credentials")
    public ResponseEntity<String> registerUser(@RequestBody UserRegistrationDto userRegistrationDto) {
        return authService.register(userRegistrationDto);
    }
}