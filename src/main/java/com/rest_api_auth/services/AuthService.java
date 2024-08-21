package com.rest_api_auth.services;

import com.rest_api_auth.models.dtos.AuthDto;
import com.rest_api_auth.models.dtos.UserRegistrationDto;
import org.springframework.http.ResponseEntity;

public interface AuthService {
    ResponseEntity<String> login(AuthDto authDto);
    ResponseEntity<String> register(UserRegistrationDto userRegistrationDto);
}
