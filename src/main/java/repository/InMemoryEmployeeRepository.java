package repository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import domain.Employee;
import exception.EmployeeNotFoundException;

public final class InMemoryEmployeeRepository implements EmployeeRepository {

    private final Map<String, Employee> employees = new HashMap<>();

    public InMemoryEmployeeRepository(Collection<Employee> employees) {
        for (Employee employee : employees) {
            this.employees.put(employee.id(), employee);
        }
    }

    @Override
    public Employee getById(String id) {
        Employee employee = employees.get(id);

        if (employee == null) {
            throw new EmployeeNotFoundException(id);
        }

        return employee;
    }
}
