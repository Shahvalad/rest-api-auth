package com.rest_api_auth.controllers;

import com.rest_api_auth.mapper.Mapper;
import com.rest_api_auth.models.dtos.HeroDto;
import com.rest_api_auth.models.entities.Hero;
import com.rest_api_auth.services.HeroService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.util.List;

@RestController
@RequestMapping("/api/heroes")
@Tag(name = "Heroes", description = "Operations related to hero management")
public class HeroesController {

    private final HeroService heroService;
    private final Mapper<Hero, HeroDto> heroMapper;

    public HeroesController(HeroService heroService, Mapper<Hero, HeroDto> heroMapper) {
        this.heroService = heroService;
        this.heroMapper = heroMapper;
    }

    @GetMapping
    @PreAuthorize("hasRole('MYADMIN')")
    @Operation(summary = "Get all heroes", description = "Retrieve a list of all heroes available in the database")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of heroes"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Access token is missing or invalid"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient privileges")
    })
    public ResponseEntity<List<HeroDto>> getHeroes() {
        var heroes = heroService.findAll();
        var heroDtos = heroes.stream()
                .map(heroMapper::entityToDto)
                .toList();
        return ResponseEntity.ok(heroDtos);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('MYADMIN')")
    @Operation(summary = "Get hero by ID", description = "Retrieve a specific hero by their unique ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved hero"),
            @ApiResponse(responseCode = "404", description = "Hero not found with the specified ID"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Access token is missing or invalid"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient privileges")
    })
    public ResponseEntity<HeroDto> getHero(@PathVariable Long id) {
        var hero = heroService.findById(id);
        var heroDto = heroMapper.entityToDto(hero);
        return ResponseEntity.ok(heroDto);
    }

    @PostMapping
    @PreAuthorize("hasRole('MYADMIN')")
    @Operation(summary = "Add a new hero", description = "Create a new hero and save it to the database")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Successfully created new hero"),
            @ApiResponse(responseCode = "400", description = "Invalid request data provided"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Access token is missing or invalid"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient privileges")
    })
    public ResponseEntity<Long> addHero(
            @Parameter(description = "Hero data to be added", required = true)
            @RequestBody @Valid HeroDto heroDto) {
        var hero = heroMapper.dtoToEntity(heroDto);
        var savedHero = heroService.save(hero);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedHero.getId());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MYADMIN')")
    @Operation(summary = "Update an existing hero", description = "Update the details of an existing hero by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Successfully updated the hero"),
            @ApiResponse(responseCode = "404", description = "Hero not found with the specified ID"),
            @ApiResponse(responseCode = "400", description = "Invalid request data provided"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Access token is missing or invalid"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient privileges")
    })
    public ResponseEntity<Void> updateHero(
            @Parameter(description = "ID of the hero to be updated", required = true)
            @PathVariable Long id,
            @Parameter(description = "Updated hero data", required = true)
            @RequestBody @Valid HeroDto heroDto) {
        var hero = heroMapper.dtoToEntity(heroDto);
        heroService.update(id, hero);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MYADMIN')")
    @Operation(summary = "Delete a hero", description = "Remove a hero from the database by their unique ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Successfully deleted the hero"),
            @ApiResponse(responseCode = "404", description = "Hero not found with the specified ID"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Access token is missing or invalid"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient privileges")
    })
    public ResponseEntity<Void> deleteHero(
            @Parameter(description = "ID of the hero to be deleted", required = true)
            @PathVariable Long id) {
        heroService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('MYADMIN')")
    @Operation(summary = "Search for heroes", description = "Search for heroes by name, power, or universe")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of heroes"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Access token is missing or invalid"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient privileges")
    })
    public ResponseEntity<List<HeroDto>> searchHeroes(
            @Parameter(description = "Name of the hero to search for")
            @RequestParam(required = false) String name,
            @Parameter(description = "Power of the hero to search for")
            @RequestParam(required = false) String power,
            @Parameter(description = "Universe of the hero to search for")
            @RequestParam(required = false) String universe) {
        var heroes = heroService.search(name, power, universe);
        var heroDtos = heroes.stream()
                .map(heroMapper::entityToDto)
                .toList();
        return ResponseEntity.ok(heroDtos);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('MYADMIN')")
    @Operation(summary = "Partially update a hero", description = "Update one or more fields of a hero by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Successfully updated the hero"),
            @ApiResponse(responseCode = "404", description = "Hero not found with the specified ID"),
            @ApiResponse(responseCode = "400", description = "Invalid request data provided"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Access token is missing or invalid"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient privileges")
    })
    public ResponseEntity<Void> updateHeroPartially(
            @Parameter(description = "ID of the hero to be updated", required = true)
            @PathVariable Long id,
            @Parameter(description = "Updated hero data", required = true)
            @RequestBody HeroDto heroDto) {
        heroService.updateHeroPartially(id, heroDto.getName(), heroDto.getPower(), heroDto.getUniverse());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/export")
    @PreAuthorize("hasRole('MYADMIN')")
    @Operation(summary = "Export heroes to CSV", description = "Export a list of heroes to CSV format")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully exported heroes"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Access token is missing or invalid"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient privileges")
    })
    public ResponseEntity<Resource> exportHeroes() {
        Resource file = heroService.exportHeroesToCsv();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=heroes.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(file);
    }
}

