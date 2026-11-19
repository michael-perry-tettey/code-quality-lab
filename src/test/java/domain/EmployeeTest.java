package domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class EmployeeTest {

    @Test
    @DisplayName("Treats employees with the same ID and name as equal")
    void treatsEmployeesWithTheSameIdAndNameAsEqual() {
        assertEquals(new Employee("e1", "Efua Sarpong"), new Employee("e1", "Efua Sarpong"));
    }

    @Test
    @DisplayName("Treats employees with the same name but different IDs as different people")
    void treatsEmployeesWithTheSameNameButDifferentIdsAsDifferentPeople() {
        assertNotEquals(new Employee("e1", "Kofi Mensah"), new Employee("e2", "Kofi Mensah"));
    }

    @ParameterizedTest
    @DisplayName("Rejects a blank ID")
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void rejectsABlankId(String id) {
        assertThrows(IllegalArgumentException.class, () -> new Employee(id, "Efua Sarpong"));
    }

    @ParameterizedTest
    @DisplayName("Rejects a blank name")
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void rejectsABlankName(String name) {
        assertThrows(IllegalArgumentException.class, () -> new Employee("e1", name));
    }
}
