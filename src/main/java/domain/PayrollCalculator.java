package domain;

import static validation.InputValidator.requireText;

import java.util.Objects;
import pay.GrossPayCalculator;
import repository.EmployeeRepository;
import tax.TaxPolicy;

public final class PayrollCalculator {

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

        double basicPay = grossPayCalculator.basicPayFor(hoursWorked);
        double overtimePay = grossPayCalculator.overtimePayFor(hoursWorked);
        double grossPay = basicPay + overtimePay;
        double tax = taxPolicy.taxFor(grossPay);
        double netPay = grossPay - tax;
        return new Payslip(employee, basicPay, overtimePay, tax, netPay);
    }
}
