package com.rest_api_auth.services.impl;

import com.rest_api_auth.exceptions.HeroNotFoundException;
import com.rest_api_auth.models.entities.Hero;
import com.rest_api_auth.repositories.HeroRepository;
import com.rest_api_auth.services.HeroService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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
    public Optional<Hero> findById(Long id) {
        return heroRepository.findById(id);
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
}
