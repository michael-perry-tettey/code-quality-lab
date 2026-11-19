package repository;

import domain.Employee;
import java.util.Optional;

public interface EmployeeRepository {
    Optional<Employee> findById(String id);
}
