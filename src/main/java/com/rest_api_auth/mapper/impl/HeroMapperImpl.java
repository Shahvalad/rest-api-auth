package com.rest_api_auth.mapper.impl;

import com.rest_api_auth.mapper.Mapper;
import com.rest_api_auth.models.dtos.HeroDto;
import com.rest_api_auth.models.entities.Hero;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class HeroMapperImpl implements Mapper<Hero, HeroDto> {

    private final ModelMapper modelMapper;

    public HeroMapperImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public HeroDto entityToDto(Hero entity) {
        return modelMapper.map(entity, HeroDto.class);
    }

    @Override
    public Hero dtoToEntity(HeroDto dto) {
        return modelMapper.map(dto, Hero.class);
    }
}
