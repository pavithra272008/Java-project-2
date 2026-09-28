package com.example.demo.service;

import com.example.demo.dto.EmployeeRequestDto;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Employee;
import com.example.demo.repository.EmployeeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Transactional(readOnly = true)
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Employee getEmployeeById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + id));
    }

    @Transactional
    public Employee createEmployee(EmployeeRequestDto dto) {
        if (employeeRepository.findByEmailIgnoreCase(dto.getEmail()).isPresent()) {
            throw new BadRequestException("Employee with email '" + dto.getEmail() + "' already exists");
        }
        if (employeeRepository.findByEmployeeCodeIgnoreCase(dto.getEmployeeCode()).isPresent()) {
            throw new BadRequestException("Employee with code '" + dto.getEmployeeCode() + "' already exists");
        }

        Employee employee = new Employee();
        employee.setEmployeeCode(dto.getEmployeeCode());
        employee.setName(dto.getName());
        employee.setEmail(dto.getEmail());
        employee.setDepartment(dto.getDepartment());
        employee.setRole(dto.getRole());
        employee.setPhone(dto.getPhone());
        employee.setAvatarUrl(dto.getAvatarUrl());

        return employeeRepository.save(employee);
    }
}
