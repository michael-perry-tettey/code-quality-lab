package repository;

import domain.Employee;
import exception.EmployeeNotFoundException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public final class InMemoryEmployeeRepository implements EmployeeRepository {

    private final Map<String, Employee> employees = new HashMap<>();

    public InMemoryEmployeeRepository(Collection<Employee> employees) {
        for (Employee employee : employees) {
            this.employees.put(employee.getId(), employee);
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
