package de.yeganegi.payroll.repository;

import de.yeganegi.payroll.model.Employee;

import java.util.Optional;

public interface EmployeeRepository {

    void save(Employee employee);

    Optional<Employee> findById(long employeeId);
}
