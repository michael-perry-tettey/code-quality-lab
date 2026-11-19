package domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.EmployeeRepository;

@ExtendWith(MockitoExtension.class)
class PayrollCalculatorTest {

    private static final double PESEWA_PRECISION = 0.0001;

    @Mock
    private EmployeeRepository repository;

    private PayrollCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new PayrollCalculator(repository);
    }

    @Test
    @DisplayName("Pays 40 hours without overtime")
    void paysFortyHoursWithoutOvertime() {
        givenEmployee("e1");
        assertEquals(760.00, calculator.calculateNetPay("e1", 40, 20), PESEWA_PRECISION);
    }

    @Test
    @DisplayName("Pays hours over 40 at time and a half")
    void paysHoursOverFortyAtTimeAndAHalf() {
        givenEmployee("e2");
        assertEquals(1402.50, calculator.calculateNetPay("e2", 50, 30), PESEWA_PRECISION);
    }

    @Test
    @DisplayName("Adds no overtime at exactly 40 hours")
    void addsNoOvertimeAtExactlyFortyHours() {
        givenEmployee("e1");
        assertEquals(380.00, calculator.calculateNetPay("e1", 40, 10), PESEWA_PRECISION);
    }

    @Test
    @DisplayName("Adds overtime from the first hour over 40")
    void addsOvertimeFromTheFirstHourOverForty() {
        givenEmployee("e1");
        assertEquals(394.25, calculator.calculateNetPay("e1", 41, 10), PESEWA_PRECISION);
    }

    @Test
    @DisplayName("Pays nothing for zero hours")
    void paysNothingForZeroHours() {
        givenEmployee("e1");
        assertEquals(0.00, calculator.calculateNetPay("e1", 0, 20), PESEWA_PRECISION);
    }

    @Test
    @DisplayName("Rounds net pay to 2 decimal places")
    void roundsNetPayToTwoDecimalPlaces() {
        givenEmployee("e1");
        assertEquals(949.99, calculator.calculateNetPay("e1", 1, 999.99), PESEWA_PRECISION);
    }

    @Test
    @DisplayName("Taxes gross of exactly 1000 at 15%")
    void taxesGrossOfExactlyOneThousandAtFifteenPercent() {
        givenEmployee("e1");
        assertEquals(850.00, calculator.calculateNetPay("e1", 1, 1000), PESEWA_PRECISION);
    }

    @Test
    @DisplayName("Taxes gross of exactly 5000 at 25%")
    void taxesGrossOfExactlyFiveThousandAtTwentyFivePercent() {
        givenEmployee("e1");
        assertEquals(3750.00, calculator.calculateNetPay("e1", 1, 5000), PESEWA_PRECISION);
    }

    @Test
    @DisplayName("Rejects an unknown employee")
    void rejectsAnUnknownEmployee() {
        assertThrows(RuntimeException.class,
                () -> calculator.calculateNetPay("unknown", 40, 20));
    }

    @ParameterizedTest
    @DisplayName("Rejects hours outside 0 to 168")
    @ValueSource(doubles = {-10, 169, Double.NaN})
    void rejectsHoursOutsideZeroToOneSixtyEight(double hours) {
        assertThrows(IllegalArgumentException.class,
                () -> calculator.calculateNetPay("e1", hours, 20));
    }

    @ParameterizedTest
    @DisplayName("Rejects an hourly rate that is not above 0")
    @ValueSource(doubles = {0, -5, Double.NaN})
    void rejectsAnHourlyRateThatIsNotAboveZero(double hourlyRate) {
        assertThrows(IllegalArgumentException.class,
                () -> calculator.calculateNetPay("e1", 40, hourlyRate));
    }

    @ParameterizedTest
    @DisplayName("Rejects a blank employee ID")
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void rejectsABlankEmployeeId(String employeeId) {
        assertThrows(IllegalArgumentException.class,
                () -> calculator.calculateNetPay(employeeId, 40, 20));
    }

    private void givenEmployee(String id) {
        when(repository.findById(id)).thenReturn(Optional.of(new Employee(id, "Test Employee")));
    }
}
