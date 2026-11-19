package report;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.List;

import domain.Employee;
import domain.PayrollCalculator;
import domain.Payslip;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pay.GrossPayCalculator;
import repository.InMemoryEmployeeRepository;
import tax.BandedTaxPolicy;

class PayrollRunTest {

    private final InMemoryEmployeeRepository employees = new InMemoryEmployeeRepository(List.of(
            new Employee("e1", "Efua Sarpong"),
            new Employee("e2", "Kwame Owusu")));

    private final PayrollRun payrollRun = new PayrollRun(new PayrollCalculator(employees,
            new GrossPayCalculator(), BandedTaxPolicy.rosewoodBands()));

    @Test
    @DisplayName("Pays each employee in the order they first appear")
    void paysEachEmployeeInTheOrderTheyFirstAppear() {
        PayrollRunReport report = payrollRun.run(List.of(
                new PayrollEntry("e2", 20, 30),
                new PayrollEntry("e1", 40, 20),
                new PayrollEntry("e2", 30, 30)));
        assertAll(
                () -> assertEquals("e2", report.payslips().get(0).employee().id()),
                () -> assertEquals("e1", report.payslips().get(1).employee().id()),
                () -> assertEquals(List.of(), report.failures()));
    }

    @Test
    @DisplayName("Combines an employee's lines into one payslip with weekly overtime")
    void combinesAnEmployeesLinesIntoOnePayslipWithWeeklyOvertime() {
        PayrollRunReport report = payrollRun.run(List.of(
                new PayrollEntry("e1", 30, 20),
                new PayrollEntry("e1", 30, 20)));
        Payslip payslip = report.payslips().get(0);
        assertAll(
                () -> assertEquals(1, report.payslips().size()),
                () -> assertEquals(new BigDecimal("800.00"), payslip.basicPay()),
                () -> assertEquals(new BigDecimal("600.00"), payslip.overtimePay()));
    }

    @Test
    @DisplayName("Fails an employee's week when their lines have different rates")
    void failsAnEmployeesWeekWhenTheirLinesHaveDifferentRates() {
        PayrollRunReport report = payrollRun.run(List.of(
                new PayrollEntry("e1", 40, 20),
                new PayrollEntry("e1", 10, 30)));
        assertAll(
                () -> assertEquals(List.of(), report.payslips()),
                () -> assertEquals(List.of(new PayrollFailure("e1",
                        "Lines for e1 have different hourly rates")), report.failures()));
    }

    @Test
    @DisplayName("Fails an employee's whole week when one line is bad")
    void failsAnEmployeesWholeWeekWhenOneLineIsBad() {
        PayrollRunReport report = payrollRun.run(List.of(
                new PayrollEntry("e1", 40, 20),
                new PayrollEntry("e1", -5, 20)));
        assertAll(
                () -> assertEquals(List.of(), report.payslips()),
                () -> assertEquals(List.of(new PayrollFailure("e1",
                        "Hours worked must be between 0.0 and 168.0, got -5.0")),
                        report.failures()));
    }

    @Test
    @DisplayName("Fails an employee whose lines add up to more than 168 hours")
    void failsAnEmployeeWhoseLinesAddUpToMoreThan168Hours() {
        PayrollRunReport report = payrollRun.run(List.of(
                new PayrollEntry("e1", 100, 20),
                new PayrollEntry("e1", 100, 20)));
        assertEquals(List.of(new PayrollFailure("e1",
                "Hours worked must be between 0.0 and 168.0, got 200.0")), report.failures());
    }

    @Test
    @DisplayName("Records an unknown employee as a failure and carries on")
    void recordsAnUnknownEmployeeAsAFailureAndCarriesOn() {
        PayrollRunReport report = payrollRun.run(List.of(
                new PayrollEntry("unknown", 40, 20),
                new PayrollEntry("e1", 40, 20)));
        assertAll(
                () -> assertEquals(1, report.payslips().size()),
                () -> assertEquals(List.of(new PayrollFailure("unknown",
                        "No employee with ID 'unknown'")), report.failures()));
    }

    @Test
    @DisplayName("Totals the net pay of the payslips only")
    void totalsTheNetPayOfThePayslipsOnly() {
        PayrollRunReport report = payrollRun.run(List.of(
                new PayrollEntry("e1", 40, 20),
                new PayrollEntry("e2", 50, 30),
                new PayrollEntry("unknown", 40, 20)));
        assertEquals(new BigDecimal("2162.50"), report.totalNetPay());
    }

    @Test
    @DisplayName("Gives an empty report with a zero total for no entries")
    void givesAnEmptyReportWithAZeroTotalForNoEntries() {
        PayrollRunReport report = payrollRun.run(List.of());
        assertAll(
                () -> assertEquals(List.of(), report.payslips()),
                () -> assertEquals(List.of(), report.failures()),
                () -> assertEquals(new BigDecimal("0.00"), report.totalNetPay()));
    }

    @Test
    @DisplayName("Gives the same report when the same week is run again")
    void givesTheSameReportWhenTheSameWeekIsRunAgain() {
        List<PayrollEntry> week = List.of(
                new PayrollEntry("e1", 40, 20),
                new PayrollEntry("unknown", 40, 20));
        PayrollRunReport first = payrollRun.run(week);
        PayrollRunReport second = payrollRun.run(week);
        assertEquals(first, second);
    }
}
