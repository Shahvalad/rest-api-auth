package com.rest_api_auth.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/heroes")
@Tag(name = "Heroes")
public class HeroesController {

    @GetMapping()
    @PreAuthorize("hasRole('MYADMIN')")
    public String getHeroes() {
        return "Batman, Superman, Wonder";
    }
}
