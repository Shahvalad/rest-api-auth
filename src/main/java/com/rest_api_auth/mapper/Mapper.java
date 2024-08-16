package com.rest_api_auth.mapper;

public interface Mapper<E,D> {
    D entityToDto(E entity);
    E dtoToEntity(D dto);
}
