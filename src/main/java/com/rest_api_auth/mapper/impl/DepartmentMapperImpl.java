package com.rest_api_auth.mapper.impl;


import com.rest_api_auth.mapper.Mapper;
import com.rest_api_auth.models.dtos.CreateDepartmentDto;
import com.rest_api_auth.models.dtos.GetDepartmentDto;
import com.rest_api_auth.models.entities.Department;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class DepartmentMapperImpl implements Mapper<Department, GetDepartmentDto> {

    private final ModelMapper modelMapper;

    public DepartmentMapperImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public GetDepartmentDto entityToDto(Department entity) {
        return modelMapper.map(entity, GetDepartmentDto.class);
    }

    @Override
    public Department dtoToEntity(GetDepartmentDto dto) {
        return modelMapper.map(dto, Department.class);
    }

    public Department dtoToEntity(CreateDepartmentDto dto) {
        return modelMapper.map(dto, Department.class);
    }
}
