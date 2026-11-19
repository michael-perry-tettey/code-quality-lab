package domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class HoursWorkedTest {

    @ParameterizedTest
    @DisplayName("Accepts hours from 0 to 168")
    @ValueSource(doubles = {0, 40, 168})
    void acceptsHoursFromZeroToOneSixtyEight(double hours) {
        HoursWorked hoursWorked = new HoursWorked(hours, 20);
        assertEquals(hours, hoursWorked.hours());
    }

    @ParameterizedTest
    @DisplayName("Rejects hours outside 0 to 168")
    @ValueSource(doubles = {-10, 169, Double.NaN})
    void rejectsHoursOutsideZeroToOneSixtyEight(double hours) {
        assertThrows(IllegalArgumentException.class, () -> new HoursWorked(hours, 20));
    }

    @ParameterizedTest
    @DisplayName("Rejects an hourly rate that is not above 0")
    @ValueSource(doubles = {0, -5, Double.NaN})
    void rejectsAnHourlyRateThatIsNotAboveZero(double hourlyRate) {
        assertThrows(IllegalArgumentException.class, () -> new HoursWorked(40, hourlyRate));
    }
}
