package com.rest_api_auth.services;

import com.rest_api_auth.models.entities.Hero;
import org.springframework.core.io.Resource;

import java.util.List;

public interface HeroService {
    List<Hero> findAll();
    Hero findById(Long id);
    Hero save(Hero hero);
    Hero update(Long id, Hero hero);
    void delete(Long id);
    List<Hero> search(String name, String power, String universe);
    Hero updateHeroPartially(Long id, String name, String power, String universe);
    Resource exportHeroesToCsv();
}
