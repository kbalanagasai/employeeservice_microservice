package com.example.employee.service;

import com.example.employee.exception.ResourceNotFoundException;
import com.example.employee.model.Employee;
import com.example.employee.repository.EmployeeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class EmployeeService {
    private final EmployeeRepository repository;

    public EmployeeService(EmployeeRepository repository) { this.repository = repository; }

    public Employee create(Employee employee) {
        employee.setEmail(employee.getEmail().trim().toLowerCase());
        if (repository.existsByEmailIgnoreCase(employee.getEmail())) {
            throw new IllegalArgumentException("Email address already exists");
        }
        return repository.save(employee);
    }

    @Transactional(readOnly = true)
    public List<Employee> findAll(Long departmentId) {
        return departmentId == null ? repository.findAll() : repository.findByDepartmentId(departmentId);
    }

    @Transactional(readOnly = true)
    public Employee findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + id));
    }

    public Employee update(Long id, Employee input) {
        Employee existing = findById(id);
        String email = input.getEmail().trim().toLowerCase();
        if (!existing.getEmail().equalsIgnoreCase(email) && repository.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException("Email address already exists");
        }
        existing.setFirstName(input.getFirstName().trim());
        existing.setLastName(input.getLastName().trim());
        existing.setEmail(email);
        existing.setJobTitle(input.getJobTitle());
        existing.setSalary(input.getSalary());
        existing.setDepartmentId(input.getDepartmentId());
        return repository.save(existing);
    }

    public void delete(Long id) {
        Employee existing = findById(id);
        repository.delete(existing);
    }
}
