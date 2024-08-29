package com.rest_api_auth.services.impl;

import com.rest_api_auth.exceptions.HeroNotFoundException;
import com.rest_api_auth.models.entities.Hero;
import com.rest_api_auth.repositories.HeroRepository;
import com.rest_api_auth.services.HeroService;
import jakarta.annotation.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

import java.nio.charset.StandardCharsets;

@Service
@Transactional
public class HeroServiceImpl implements HeroService {

    private final HeroRepository heroRepository;

    public HeroServiceImpl(HeroRepository heroRepository) {
        this.heroRepository = heroRepository;
    }

    @Override
    public List<Hero> findAll() {
        return heroRepository.findAll();
    }

    @Override
    public Hero findById(Long id) {
        return heroRepository.findById(id)
                .orElseThrow(() -> new HeroNotFoundException("Hero not found with ID: " + id));
    }

    @Override
    public Hero save(Hero hero) {
        return heroRepository.save(hero);
    }

    @Override
    public Hero update(Long id, Hero hero) {
        if (!heroRepository.existsById(id)) {
            throw new HeroNotFoundException("Hero not found with id: " + id);
        }
        hero.setId(id);
        return heroRepository.save(hero);
    }

    @Override
    public void delete(Long id) {
        if (!heroRepository.existsById(id)) {
            throw new HeroNotFoundException("Hero not found with id: " + id);
        }
        heroRepository.deleteById(id);
    }

    @Override
    public List<Hero> search(@Nullable String name, @Nullable String power, @Nullable String universe) {
        var heroes = heroRepository.findAll();
        return heroes.stream()
                .filter(hero -> (name == null || hero.getName().toLowerCase().contains(name.toLowerCase())) &&
                                (power == null || hero.getPower().toLowerCase().contains(power.toLowerCase())) &&
                                (universe == null || hero.getUniverse().toLowerCase().contains(universe.toLowerCase())))
                .collect(Collectors.toList());
    }

    @Override
    public Hero updateHeroPartially(Long id, @Nullable String name, @Nullable String power, @Nullable String universe) {
        var hero = heroRepository.findById(id)
                .orElseThrow(() -> new HeroNotFoundException("Hero not found with ID: " + id));
        if (name != null) { hero.setName(name); }
        if (power != null) { hero.setPower(power); }
        if (universe != null) { hero.setUniverse(universe); }
        return heroRepository.save(hero);
    }

    @Override
    public Resource exportHeroesToCsv() {
        var heroes = heroRepository.findAll();
        var csv = new StringBuilder();
        csv.append("Name,Power,Universe\n");
        heroes.forEach(hero -> csv.append(hero.getName()).append(",")
                .append(hero.getPower()).append(",")
                .append(hero.getUniverse()).append("\n"));
        return new ByteArrayResource(csv.toString().getBytes(StandardCharsets.UTF_8));
    }


}
