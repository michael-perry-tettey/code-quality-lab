package application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import domain.Employee;
import domain.HoursWorked;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import pay.GrossPayCalculator;
import repository.EmployeeRepository;
import tax.TaxPolicy;

class PayrollCalculatorUnitTest {

    @Test
    void calculatesAndRoundsPayslipUsingCollaborators() {
        EmployeeRepository repository = org.mockito.Mockito.mock(EmployeeRepository.class);
        GrossPayCalculator grossPay = org.mockito.Mockito.mock(GrossPayCalculator.class);
        TaxPolicy taxPolicy = org.mockito.Mockito.mock(TaxPolicy.class);
        when(repository.getById("e1")).thenReturn(new Employee("e1", "Test Employee"));
        when(grossPay.basicPayFor(new HoursWorked(40, 20))).thenReturn(new BigDecimal("800.005"));
        when(grossPay.overtimePayFor(new HoursWorked(40, 20))).thenReturn(new BigDecimal("0"));
        when(taxPolicy.taxFor(new BigDecimal("800.01"))).thenReturn(new BigDecimal("40.000"));

        var payslip = new PayrollCalculator(repository, grossPay, taxPolicy)
                .calculatePayslip("e1", new HoursWorked(40, 20));

        assertEquals(new BigDecimal("800.01"), payslip.basicPay());
        assertEquals(new BigDecimal("40.00"), payslip.tax());
        assertEquals(new BigDecimal("760.01"), payslip.netPay());
    }
}
