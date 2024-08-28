package com.rest_api_auth.services;

import com.rest_api_auth.models.entities.Department;

import java.util.List;

public interface DepartmentService {
    List<Department> findAll();
    Department findById(Long id);
    Department save(Department department);
    Department update(Long id, Department department);
    void delete(Long id);
}
