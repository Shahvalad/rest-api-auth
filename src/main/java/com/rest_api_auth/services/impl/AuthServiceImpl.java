package com.rest_api_auth.services.impl;

import com.rest_api_auth.models.dtos.AuthDto;
import com.rest_api_auth.models.dtos.KeycloakUserDto;
import com.rest_api_auth.models.dtos.UserRegistrationDto;
import com.rest_api_auth.services.AuthService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

@Service
public class AuthServiceImpl implements AuthService {

    @Value("${keycloak.admin.client-id}")
    private String clientId;

    @Value("${keycloak.admin.username}")
    private String adminUsername;

    @Value("${keycloak.admin.password}")
    private String adminPassword;

    @Value("${keycloak.urls.token}")
    private String tokenUrl;

    @Value("${keycloak.urls.register}")
    private String registerUrl;

    @Value("${keycloak.urls.auth-server}")
    private String authServerUrl;

    @Value("${keycloak.admin.grant-type}")
    private String grantType;

    private final RestTemplate restTemplate;

    @Autowired
    public AuthServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ResponseEntity<String> login(AuthDto authDto) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        String body = "client_id=" + clientId + "&" +
                "username=" + authDto.login() + "&" +
                "password=" + authDto.password() + "&" +
                "grant_type=" + grantType;

        HttpEntity<String> requestEntity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    authServerUrl + tokenUrl,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error: Wrong credentials");
        }
    }

    public ResponseEntity<String> register(UserRegistrationDto userRegistrationDto) {
        String token = getAdminToken();

        KeycloakUserDto keycloakUserDto = new KeycloakUserDto();
        keycloakUserDto.setUsername(userRegistrationDto.getUsername());
        keycloakUserDto.setEmail(userRegistrationDto.getEmail());
        keycloakUserDto.setFirstName(userRegistrationDto.getFirstName());
        keycloakUserDto.setLastName(userRegistrationDto.getLastName());
        keycloakUserDto.setEnabled(true);

        KeycloakUserDto.Credential credential = new KeycloakUserDto.Credential();
        credential.setType("password");
        credential.setValue(userRegistrationDto.getPassword());
        credential.setTemporary(false);

        keycloakUserDto.setCredentials(List.of(credential));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(token);

        HttpEntity<KeycloakUserDto> request = new HttpEntity<>(keycloakUserDto, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    authServerUrl + registerUrl,
                    HttpMethod.POST,
                    request,
                    String.class
            );
            if (response.getStatusCode() == HttpStatus.CREATED) {
                return ResponseEntity.ok("User registered successfully");
            } else {
                return ResponseEntity.status(response.getStatusCode()).body("Error registering user: " + response.getBody());
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    private String getAdminToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String body = "client_id=" + clientId + "&" +
                "username=" + adminUsername + "&" +
                "password=" + adminPassword + "&" +
                "grant_type=password";

        HttpEntity<String> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                    authServerUrl + tokenUrl,
                    request,
                    String.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                JSONObject json = new JSONObject(response.getBody());
                return json.getString("access_token");
            } else {
                throw new RuntimeException("Failed to obtain access token. Status code: " + response.getStatusCode());
            }
        } catch (Exception e) {
            throw new RuntimeException("Exception while obtaining access token: " + e.getMessage(), e);
        }
    }
}
