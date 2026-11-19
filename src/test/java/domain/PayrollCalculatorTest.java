package domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import exception.EmployeeNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pay.GrossPayCalculator;
import repository.EmployeeRepository;
import tax.BandedTaxPolicy;

@ExtendWith(MockitoExtension.class)
class PayrollCalculatorTest {

    private static final double PESEWA_PRECISION = 0.0001;

    @Mock
    private EmployeeRepository repository;

    private PayrollCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new PayrollCalculator(repository, new GrossPayCalculator(),
                BandedTaxPolicy.rosewoodBands());
    }

    @Test
    @DisplayName("Pays 40 hours without overtime")
    void paysFortyHoursWithoutOvertime() {
        givenEmployee("e1");
        double netPay = calculator.calculateNetPay("e1", new HoursWorked(40, 20));
        assertEquals(760.00, netPay, PESEWA_PRECISION);
    }

    @Test
    @DisplayName("Pays hours over 40 at time and a half")
    void paysHoursOverFortyAtTimeAndAHalf() {
        givenEmployee("e2");
        double netPay = calculator.calculateNetPay("e2", new HoursWorked(50, 30));
        assertEquals(1402.50, netPay, PESEWA_PRECISION);
    }

    @Test
    @DisplayName("Adds no overtime at exactly 40 hours")
    void addsNoOvertimeAtExactlyFortyHours() {
        givenEmployee("e1");
        double netPay = calculator.calculateNetPay("e1", new HoursWorked(40, 10));
        assertEquals(380.00, netPay, PESEWA_PRECISION);
    }

    @Test
    @DisplayName("Adds overtime from the first hour over 40")
    void addsOvertimeFromTheFirstHourOverForty() {
        givenEmployee("e1");
        double netPay = calculator.calculateNetPay("e1", new HoursWorked(41, 10));
        assertEquals(394.25, netPay, PESEWA_PRECISION);
    }

    @Test
    @DisplayName("Pays nothing for zero hours")
    void paysNothingForZeroHours() {
        givenEmployee("e1");
        double netPay = calculator.calculateNetPay("e1", new HoursWorked(0, 20));
        assertEquals(0.00, netPay, PESEWA_PRECISION);
    }

    @Test
    @DisplayName("Rounds net pay to 2 decimal places")
    void roundsNetPayToTwoDecimalPlaces() {
        givenEmployee("e1");
        double netPay = calculator.calculateNetPay("e1", new HoursWorked(1, 999.99));
        assertEquals(949.99, netPay, PESEWA_PRECISION);
    }

    @Test
    @DisplayName("Taxes gross of exactly 1000 at 15%")
    void taxesGrossOfExactlyOneThousandAtFifteenPercent() {
        givenEmployee("e1");
        double netPay = calculator.calculateNetPay("e1", new HoursWorked(1, 1000));
        assertEquals(850.00, netPay, PESEWA_PRECISION);
    }

    @Test
    @DisplayName("Taxes gross of exactly 5000 at 25%")
    void taxesGrossOfExactlyFiveThousandAtTwentyFivePercent() {
        givenEmployee("e1");
        double netPay = calculator.calculateNetPay("e1", new HoursWorked(1, 5000));
        assertEquals(3750.00, netPay, PESEWA_PRECISION);
    }

    @Test
    @DisplayName("Rejects an unknown employee")
    void rejectsAnUnknownEmployee() {
        givenNoEmployee("unknown");
        HoursWorked hoursWorked = new HoursWorked(40, 20);
        assertThrows(EmployeeNotFoundException.class,
                () -> calculator.calculateNetPay("unknown", hoursWorked));
    }

    @Test
    @DisplayName("Names the missing employee in the error")
    void namesTheMissingEmployeeInTheError() {
        givenNoEmployee("unknown");
        HoursWorked hoursWorked = new HoursWorked(40, 20);
        EmployeeNotFoundException error = assertThrows(EmployeeNotFoundException.class,
                () -> calculator.calculateNetPay("unknown", hoursWorked));
        assertEquals("No employee with ID 'unknown'", error.getMessage());
    }

    @ParameterizedTest
    @DisplayName("Rejects a blank employee ID")
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void rejectsABlankEmployeeId(String employeeId) {
        HoursWorked hoursWorked = new HoursWorked(40, 20);
        assertThrows(IllegalArgumentException.class,
                () -> calculator.calculateNetPay(employeeId, hoursWorked));
    }

    private void givenEmployee(String id) {
        when(repository.getById(id)).thenReturn(new Employee(id, "Test Employee"));
    }

    private void givenNoEmployee(String id) {
        when(repository.getById(id)).thenThrow(new EmployeeNotFoundException(id));
    }
}
