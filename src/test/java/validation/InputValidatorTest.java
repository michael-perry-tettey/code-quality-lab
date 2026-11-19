package validation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class InputValidatorTest {

    @Test
    @DisplayName("Returns text that is not blank")
    void returnsTextThatIsNotBlank() {
        assertEquals("e1", InputValidator.requireText("e1", "Employee ID"));
    }

    @ParameterizedTest
    @DisplayName("Rejects blank text, naming the value")
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void rejectsBlankTextNamingTheValue(String value) {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> InputValidator.requireText(value, "Employee ID"));
        assertEquals("Employee ID must not be blank", error.getMessage());
    }

    @ParameterizedTest
    @DisplayName("Accepts values from the minimum to the maximum")
    @ValueSource(doubles = {0, 84, 168})
    void acceptsValuesFromTheMinimumToTheMaximum(double value) {
        assertEquals(value, InputValidator.requireBetween(value, 0, 168, "Hours worked"));
    }

    @ParameterizedTest
    @DisplayName("Rejects values outside the range or not a number")
    @ValueSource(doubles = {-0.01, 168.01, Double.NaN})
    void rejectsValuesOutsideTheRangeOrNotANumber(double value) {
        assertThrows(IllegalArgumentException.class,
                () -> InputValidator.requireBetween(value, 0, 168, "Hours worked"));
    }

    @Test
    @DisplayName("Names the value, the range and the bad input in the error")
    void namesTheValueTheRangeAndTheBadInputInTheError() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> InputValidator.requireBetween(-10, 0, 168, "Hours worked"));
        assertEquals("Hours worked must be between 0.0 and 168.0, got -10.0", error.getMessage());
    }

    @Test
    @DisplayName("Returns a positive value")
    void returnsAPositiveValue() {
        assertEquals(0.01, InputValidator.requirePositive(0.01, "Hourly rate"));
    }

    @ParameterizedTest
    @DisplayName("Rejects zero, negative or not-a-number values")
    @ValueSource(doubles = {0, -5, Double.NaN})
    void rejectsZeroNegativeOrNotANumberValues(double value) {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> InputValidator.requirePositive(value, "Hourly rate"));
        assertEquals("Hourly rate must be more than 0, got " + value, error.getMessage());
    }
}
