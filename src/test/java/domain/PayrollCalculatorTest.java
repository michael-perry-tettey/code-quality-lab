package domain;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

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
        Payslip payslip = calculator.calculatePayslip("e1", new HoursWorked(40, 20));
        assertEquals(new BigDecimal("760.00"), payslip.netPay());
    }

    @Test
    @DisplayName("Pays hours over 40 at time and a half")
    void paysHoursOverFortyAtTimeAndAHalf() {
        givenEmployee("e2");
        Payslip payslip = calculator.calculatePayslip("e2", new HoursWorked(50, 30));
        assertEquals(new BigDecimal("1402.50"), payslip.netPay());
    }

    @Test
    @DisplayName("Adds no overtime at exactly 40 hours")
    void addsNoOvertimeAtExactlyFortyHours() {
        givenEmployee("e1");
        Payslip payslip = calculator.calculatePayslip("e1", new HoursWorked(40, 10));
        assertEquals(new BigDecimal("380.00"), payslip.netPay());
    }

    @Test
    @DisplayName("Adds overtime from the first hour over 40")
    void addsOvertimeFromTheFirstHourOverForty() {
        givenEmployee("e1");
        Payslip payslip = calculator.calculatePayslip("e1", new HoursWorked(41, 10));
        assertEquals(new BigDecimal("394.25"), payslip.netPay());
    }

    @Test
    @DisplayName("Pays nothing for zero hours")
    void paysNothingForZeroHours() {
        givenEmployee("e1");
        Payslip payslip = calculator.calculatePayslip("e1", new HoursWorked(0, 20));
        assertEquals(new BigDecimal("0.00"), payslip.netPay());
    }

    @Test
    @DisplayName("Rounds net pay to 2 decimal places")
    void roundsNetPayToTwoDecimalPlaces() {
        givenEmployee("e1");
        Payslip payslip = calculator.calculatePayslip("e1", new HoursWorked(1, 999.99));
        assertEquals(new BigDecimal("949.99"), payslip.netPay());
    }

    @Test
    @DisplayName("Rounds each payslip amount half up so the payslip adds up")
    void roundsEachPayslipAmountHalfUpSoThePayslipAddsUp() {
        givenEmployee("e1");
        Payslip payslip = calculator.calculatePayslip("e1", new HoursWorked(37.5, 19.99));
        assertAll(
                () -> assertEquals(new BigDecimal("749.63"), payslip.basicPay()),
                () -> assertEquals(new BigDecimal("37.48"), payslip.tax()),
                () -> assertEquals(new BigDecimal("712.15"), payslip.netPay()));
    }

    @Test
    @DisplayName("Taxes gross of exactly 1000 at 15%")
    void taxesGrossOfExactlyOneThousandAtFifteenPercent() {
        givenEmployee("e1");
        Payslip payslip = calculator.calculatePayslip("e1", new HoursWorked(1, 1000));
        assertEquals(new BigDecimal("850.00"), payslip.netPay());
    }

    @Test
    @DisplayName("Taxes gross of exactly 5000 at 25%")
    void taxesGrossOfExactlyFiveThousandAtTwentyFivePercent() {
        givenEmployee("e1");
        Payslip payslip = calculator.calculatePayslip("e1", new HoursWorked(1, 5000));
        assertEquals(new BigDecimal("3750.00"), payslip.netPay());
    }

    @Test
    @DisplayName("Puts the employee on the payslip")
    void putsTheEmployeeOnThePayslip() {
        givenEmployee("e1");
        Payslip payslip = calculator.calculatePayslip("e1", new HoursWorked(40, 20));
        assertEquals("e1", payslip.employee().id());
    }

    @Test
    @DisplayName("Breaks the payslip into basic pay, overtime, tax and net pay")
    void breaksThePayslipIntoBasicPayOvertimeTaxAndNetPay() {
        givenEmployee("e2");
        Payslip payslip = calculator.calculatePayslip("e2", new HoursWorked(50, 30));
        assertAll(
                () -> assertEquals(new BigDecimal("1200.00"), payslip.basicPay()),
                () -> assertEquals(new BigDecimal("450.00"), payslip.overtimePay()),
                () -> assertEquals(new BigDecimal("1650.00"), payslip.grossPay()),
                () -> assertEquals(new BigDecimal("247.50"), payslip.tax()),
                () -> assertEquals(new BigDecimal("1402.50"), payslip.netPay()));
    }

    @Test
    @DisplayName("Gives the same payslip for the same input")
    void givesTheSamePayslipForTheSameInput() {
        givenEmployee("e1");
        HoursWorked hoursWorked = new HoursWorked(45, 20);
        Payslip first = calculator.calculatePayslip("e1", hoursWorked);
        Payslip second = calculator.calculatePayslip("e1", hoursWorked);
        assertEquals(first, second);
    }

    @Test
    @DisplayName("Rejects an unknown employee")
    void rejectsAnUnknownEmployee() {
        givenNoEmployee("unknown");
        HoursWorked hoursWorked = new HoursWorked(40, 20);
        assertThrows(EmployeeNotFoundException.class,
                () -> calculator.calculatePayslip("unknown", hoursWorked));
    }

    @Test
    @DisplayName("Names the missing employee in the error")
    void namesTheMissingEmployeeInTheError() {
        givenNoEmployee("unknown");
        HoursWorked hoursWorked = new HoursWorked(40, 20);
        EmployeeNotFoundException error = assertThrows(EmployeeNotFoundException.class,
                () -> calculator.calculatePayslip("unknown", hoursWorked));
        assertEquals("No employee with ID 'unknown'", error.getMessage());
    }

    @ParameterizedTest
    @DisplayName("Rejects a blank employee ID")
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void rejectsABlankEmployeeId(String employeeId) {
        HoursWorked hoursWorked = new HoursWorked(40, 20);
        assertThrows(IllegalArgumentException.class,
                () -> calculator.calculatePayslip(employeeId, hoursWorked));
    }

    private void givenEmployee(String id) {
        when(repository.getById(id)).thenReturn(new Employee(id, "Test Employee"));
    }

    private void givenNoEmployee(String id) {
        when(repository.getById(id)).thenThrow(new EmployeeNotFoundException(id));
    }
}
