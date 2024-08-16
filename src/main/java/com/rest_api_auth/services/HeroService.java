package com.rest_api_auth.services;

import com.rest_api_auth.models.entities.Hero;

import java.util.List;
import java.util.Optional;

public interface HeroService {
    List<Hero> findAll();
    Optional<Hero> findById(Long id);
    Hero save(Hero hero);
    Hero update(Long id, Hero hero);
    void delete(Long id);
}
