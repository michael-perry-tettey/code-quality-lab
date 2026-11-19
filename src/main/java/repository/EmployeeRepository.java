package repository;

import domain.Employee;

public interface EmployeeRepository {
    Employee findById(String id);
}
