package com.rest_api_auth.models.dtos;

import lombok.Data;

@Data
public class UserRegistrationDto {
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String password;
}
