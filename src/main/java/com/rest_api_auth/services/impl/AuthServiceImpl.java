package com.rest_api_auth.services.impl;

import com.rest_api_auth.models.dtos.AuthDto;
import com.rest_api_auth.models.dtos.UserRegistrationDto;
import com.rest_api_auth.services.AuthService;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.ws.rs.core.Response;
import java.util.Collections;

@Service
public class AuthServiceImpl implements AuthService {

    @Value("${keycloak.admin.client-id}")
    private String clientId;

    @Value("${keycloak.urls.token}")
    private String tokenUrl;

    @Value("${keycloak.urls.auth-server}")
    private String authServerUrl;

    @Value("${keycloak.admin.grant-type}")
    private String grantType;

    private final RestTemplate restTemplate;
    private final Keycloak keycloak;
    private final String realm;

    @Autowired
    public AuthServiceImpl(RestTemplate restTemplate, Keycloak keycloak, @Value("${keycloak.admin.realm}") String realm) {
        this.restTemplate = restTemplate;
        this.keycloak = keycloak;
        this.realm = realm;
    }

    @Override
    public ResponseEntity<String> login(AuthDto authDto) {
        try {
            String responseBody = requestToken(authDto);
            return ResponseEntity.ok(responseBody);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @Override
    public ResponseEntity<String> register(UserRegistrationDto userRegistrationDto) {
        Response response = createUser(userRegistrationDto);
        if (response.getStatus() == Response.Status.CREATED.getStatusCode()) {
            String userId = getUserId(userRegistrationDto.getUsername());
            assignRoleToUser(userId, "MYUSER");
            return ResponseEntity.ok("User registered successfully with role MYUSER");
        } else {
            return ResponseEntity.status(response.getStatus()).body("Error registering user: " + response.getStatusInfo());
        }
    }

    private String requestToken(AuthDto authDto) {
        HttpHeaders headers = createFormUrlEncodedHeaders();
        String body = buildTokenRequestBody(authDto);
        HttpEntity<String> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                authServerUrl + tokenUrl,
                HttpMethod.POST,
                requestEntity,
                String.class
        );
        return response.getBody();
    }

    private HttpHeaders createFormUrlEncodedHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        return headers;
    }

    private String buildTokenRequestBody(AuthDto authDto) {
        return "client_id=" + clientId + "&" +
                "username=" + authDto.login() + "&" +
                "password=" + authDto.password() + "&" +
                "grant_type=" + grantType;
    }

    private Response createUser(UserRegistrationDto userRegistrationDto) {
        UserRepresentation user = buildUserRepresentation(userRegistrationDto);
        return keycloak.realm(realm).users().create(user);
    }

    private UserRepresentation buildUserRepresentation(UserRegistrationDto userRegistrationDto) {
        UserRepresentation user = new UserRepresentation();
        user.setUsername(userRegistrationDto.getUsername());
        user.setEmail(userRegistrationDto.getEmail());
        user.setFirstName(userRegistrationDto.getFirstName());
        user.setLastName(userRegistrationDto.getLastName());
        user.setEnabled(true);

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setTemporary(false);
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(userRegistrationDto.getPassword());
        user.setCredentials(Collections.singletonList(credential));

        return user;
    }

    private String getUserId(String username) {
        return keycloak.realm(realm).users().search(username).get(0).getId();
    }

    private void assignRoleToUser(String userId, String roleName) {
        RoleRepresentation role = keycloak.realm(realm).roles().get(roleName).toRepresentation();
        keycloak.realm(realm).users().get(userId).roles().realmLevel().add(Collections.singletonList(role));
    }
}
