package com.rest_api_auth.services.impl;

import com.rest_api_auth.exceptions.DepartmentNotFoundException;
import com.rest_api_auth.models.entities.Department;
import com.rest_api_auth.repositories.DepartmentRepository;
import com.rest_api_auth.services.DepartmentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;

    public DepartmentServiceImpl(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    @Override
    public List<Department> findAll() {
        return departmentRepository.findAll();
    }

    @Override
    public Department findById(Long id) {
        return departmentRepository.findById(id).
                orElseThrow(() -> new DepartmentNotFoundException("Department not found with ID: " + id));
    }

    @Override
    public Department save(Department department) {
        return departmentRepository.save(department);
    }

    @Override
    public Department update(Long id, Department department) {
        if (!departmentRepository.existsById(id)) {
            throw new DepartmentNotFoundException("Department not found with id: " + id);
        }
        department.setId(id);
        return departmentRepository.save(department);
    }

    @Override
    public void delete(Long id) {
        if (!departmentRepository.existsById(id)) {
            throw new DepartmentNotFoundException("Department not found with id: " + id);
        }
        departmentRepository.deleteById(id);
    }
}
