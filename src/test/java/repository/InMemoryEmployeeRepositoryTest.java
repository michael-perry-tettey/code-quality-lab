package repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import domain.Employee;
import exception.EmployeeNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class InMemoryEmployeeRepositoryTest {

    private final Employee efua = new Employee("e1", "Efua Sarpong");

    private final InMemoryEmployeeRepository repository =
            new InMemoryEmployeeRepository(List.of(efua));

    @Test
    @DisplayName("Finds an employee by ID")
    void findsAnEmployeeById() {
        assertEquals(efua, repository.getById("e1"));
    }

    @Test
    @DisplayName("Throws for an unknown ID")
    void throwsForAnUnknownId() {
        assertThrows(EmployeeNotFoundException.class, () -> repository.getById("unknown"));
    }
}
