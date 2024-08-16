package com.rest_api_auth.controllers;

import com.rest_api_auth.mapper.Mapper;
import com.rest_api_auth.models.dtos.HeroDto;
import com.rest_api_auth.models.entities.Hero;
import com.rest_api_auth.services.HeroService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/heroes")
@Tag(name = "Heroes")
public class HeroesController {

    private final HeroService heroService;
    private final Mapper<Hero, HeroDto> heroMapper;

    public HeroesController(HeroService heroService, Mapper<Hero, HeroDto> heroMapper) {
        this.heroService = heroService;
        this.heroMapper = heroMapper;
    }

    @GetMapping()
    @PreAuthorize("hasRole('MYADMIN')")
    public ResponseEntity<List<HeroDto>> getHeroes() {
        var heroes = heroService.findAll();
        var heroDtos = heroes.stream()
                .map(heroMapper::entityToDto)
                .toList();
        return ResponseEntity.ok(heroDtos);
    }

    @PostMapping
    @PreAuthorize("hasRole('MYADMIN')")
    public ResponseEntity<Long> addHero(HeroDto heroDto) {
        var hero = heroMapper.dtoToEntity(heroDto);
        var savedHero = heroService.save(hero);
        return ResponseEntity.ok(savedHero.getId());
    }
}
