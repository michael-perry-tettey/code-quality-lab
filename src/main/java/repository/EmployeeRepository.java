package repository;

import domain.Employee;

public interface EmployeeRepository {

    /** Returns the employee with this ID; throws EmployeeNotFoundException if there is none. */
    Employee getById(String id);
}
