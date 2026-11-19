package domain;

import static validation.InputValidator.requireText;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import pay.GrossPayCalculator;
import repository.EmployeeRepository;
import tax.TaxPolicy;

public final class PayrollCalculator {

    private static final int PESEWA_DECIMAL_PLACES = 2;

    private final EmployeeRepository repository;
    private final GrossPayCalculator grossPayCalculator;
    private final TaxPolicy taxPolicy;

    public PayrollCalculator(EmployeeRepository repository, GrossPayCalculator grossPayCalculator,
            TaxPolicy taxPolicy) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.grossPayCalculator = Objects.requireNonNull(grossPayCalculator, "grossPayCalculator");
        this.taxPolicy = Objects.requireNonNull(taxPolicy, "taxPolicy");
    }

    public Payslip calculatePayslip(String employeeId, HoursWorked hoursWorked) {
        requireText(employeeId, "Employee ID");
        Employee employee = repository.getById(employeeId);

        BigDecimal basicPay = toPesewas(grossPayCalculator.basicPayFor(hoursWorked));
        BigDecimal overtimePay = toPesewas(grossPayCalculator.overtimePayFor(hoursWorked));
        BigDecimal grossPay = basicPay.add(overtimePay);
        BigDecimal tax = toPesewas(taxPolicy.taxFor(grossPay));
        BigDecimal netPay = grossPay.subtract(tax);
        return new Payslip(employee, basicPay, overtimePay, tax, netPay);
    }

    private static BigDecimal toPesewas(BigDecimal amount) {
        return amount.setScale(PESEWA_DECIMAL_PLACES, RoundingMode.HALF_UP);
    }
}
