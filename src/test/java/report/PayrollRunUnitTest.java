package report;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import application.PayrollCalculator;
import domain.Employee;
import domain.HoursWorked;
import domain.Payslip;
import exception.EmployeeNotFoundException;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;

class PayrollRunUnitTest {

    @Test
    void groupsLinesAndRecordsExpectedFailuresWhileContinuing() {
        PayrollCalculator calculator = mock(PayrollCalculator.class);
        Payslip payslip = new Payslip(new Employee("e1", "Test Employee"),
                new BigDecimal("800.00"), BigDecimal.ZERO, new BigDecimal("40.00"),
                new BigDecimal("760.00"));
        when(calculator.calculatePayslip("e1", new HoursWorked(40, 20))).thenReturn(payslip);
        when(calculator.calculatePayslip("missing", new HoursWorked(40, 20)))
                .thenThrow(new EmployeeNotFoundException("missing"));

        PayrollRunReport report = new PayrollRun(calculator).run(List.of(
                new PayrollEntry("e1", 20, 20), new PayrollEntry("missing", 40, 20),
                new PayrollEntry("e1", 20, 20)));

        assertEquals(List.of(payslip), report.payslips());
        assertEquals(List.of(new PayrollFailure("missing", "No employee with ID 'missing'")),
                report.failures());
    }
}
