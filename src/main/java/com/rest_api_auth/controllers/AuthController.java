package com.rest_api_auth.controllers;

import com.rest_api_auth.models.dtos.AuthDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;


@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication")
public class AuthController {

    @Value("${client-id}")
    private String clientId;
    @Value("${resource-url}")
    private String resourceUrl;
    @Value("${grant-type}")
    private String grantType;

    @PostMapping()
    @Operation(summary = "Authenticate user", description = "Authenticate user with provided credentials")
    public String auth(@RequestBody AuthDto authDto) {
        System.out.println("Received auth request for user: " + authDto.login());

        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        var body =  "client_id=" + clientId + "&" +
                "username=" + authDto.login() + "&" +
                "password=" + authDto.password() + "&" +
                "grant_type=" + grantType;
        var requestEntity = new HttpEntity<>(body, headers);
        var restTemplate = new RestTemplate();

        try {
            var response = restTemplate.exchange(
                    resourceUrl,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );
            return response.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: Wrong credentials";
        }
    }
}