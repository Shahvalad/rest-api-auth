package com.rest_api_auth.controllers;

import com.rest_api_auth.mapper.impl.DepartmentMapperImpl;
import com.rest_api_auth.models.dtos.CreateDepartmentDto;
import com.rest_api_auth.models.dtos.GetDepartmentDto;
import com.rest_api_auth.services.DepartmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/departments")
@Tag(name = "Departments", description = "Departments API")
public class DepartmentsController {

    private final DepartmentService departmentService;
    private final DepartmentMapperImpl departmentMapper;

    public DepartmentsController(DepartmentService departmentService, DepartmentMapperImpl departmentMapper) {
        this.departmentService = departmentService;
        this.departmentMapper = departmentMapper;
    }

    @GetMapping
    @Operation(summary = "Get all departments", description = "Retrieve a list of all departments available in the database")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of departments"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Access token is missing or invalid"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient privileges")
    })
    public ResponseEntity<List<GetDepartmentDto>> getDepartments() {
        var departments = departmentService.findAll();
        var departmentDtos = departments.stream()
                .map(departmentMapper::entityToDto)
                .toList();
        return ResponseEntity.ok(departmentDtos);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get department by ID", description = "Retrieve a specific department by their unique ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved department"),
            @ApiResponse(responseCode = "404", description = "Department not found with the specified ID"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Access token is missing or invalid"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient privileges")
    })
    public ResponseEntity<GetDepartmentDto> getDepartment(
            @PathVariable @Parameter(description = "ID of the department to retrieve", required = true) Long id) {
        var department = departmentService.findById(id);
        return ResponseEntity.ok(departmentMapper.entityToDto(department));
    }

    @PostMapping
    @Operation(summary = "Create a new department", description = "Create a new department with the specified details")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Department created successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request - Invalid department details"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Access token is missing or invalid"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient privileges")
    })
    public ResponseEntity<Long> createDepartment(
            @RequestBody @Valid @Parameter(description = "Details of the department to create", required = true) CreateDepartmentDto createDepartmentDto) {
        var department = departmentMapper.dtoToEntity(createDepartmentDto);
        var createdDepartment = departmentService.save(department);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDepartment.getId());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update department by ID", description = "Update the details of a specific department by their unique ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Department updated successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request - Invalid department details"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Access token is missing or invalid"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient privileges")
    })
    public ResponseEntity<Void> updateDepartment(
            @PathVariable @Parameter(description = "ID of the department to update", required = true) Long id,
            @RequestBody @Valid @Parameter(description = "Updated details of the department", required = true) CreateDepartmentDto createDepartmentDto) {
        var department = departmentMapper.dtoToEntity(createDepartmentDto);
        departmentService.update(id, department);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete department by ID", description = "Delete a specific department by their unique ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Department deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Department not found with the specified ID"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Access token is missing or invalid"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient privileges")
    })
    public ResponseEntity<Void> deleteDepartment(
            @PathVariable @Parameter(description = "ID of the department to delete", required = true) Long id) {
        departmentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
