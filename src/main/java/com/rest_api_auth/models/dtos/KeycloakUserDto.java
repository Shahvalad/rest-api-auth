package com.rest_api_auth.models.dtos;

import lombok.Data;
import java.util.List;

@Data
public class KeycloakUserDto {

    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private boolean enabled;
    private List<Credential> credentials;

    @Data
    public static class Credential {
        private String type;
        private String value;
        private boolean temporary;
    }
}
