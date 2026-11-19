package repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import domain.Employee;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class InMemoryEmployeeRepositoryTest {

    private final Employee efua = new Employee("e1", "Efua Sarpong");

    private final InMemoryEmployeeRepository repository =
            new InMemoryEmployeeRepository(List.of(efua));

    @Test
    @DisplayName("Finds an employee by ID")
    void findsAnEmployeeById() {
        assertEquals(efua, repository.findById("e1").orElseThrow());
    }

    @Test
    @DisplayName("Finds nothing for an unknown ID")
    void findsNothingForAnUnknownId() {
        assertTrue(repository.findById("unknown").isEmpty());
    }
}
