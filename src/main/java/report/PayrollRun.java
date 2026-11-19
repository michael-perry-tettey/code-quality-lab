package report;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import application.PayrollCalculator;
import domain.HoursWorked;
import domain.Payslip;
import exception.EmployeeNotFoundException;
import validation.PayrollValidationException;

/**
 * Pays a week's entries. Each employee's lines are combined and paid once on the week's total,
 * so overtime is weekly. A failure covers that employee's whole week; the run carries on.
 */
public final class PayrollRun {

    private final PayrollCalculator calculator;

    public PayrollRun(PayrollCalculator calculator) {
        this.calculator = Objects.requireNonNull(calculator, "calculator");
    }

    public PayrollRunReport run(List<PayrollEntry> entries) {
        Map<String, List<PayrollEntry>> linesByEmployee = groupByEmployee(entries);
        List<Payslip> payslips = new ArrayList<>();
        List<PayrollFailure> failures = new ArrayList<>();

        for (Map.Entry<String, List<PayrollEntry>> employeeLines : linesByEmployee.entrySet()) {
            String employeeId = employeeLines.getKey();

            try {
                HoursWorked weeklyHours = combineLines(employeeId, employeeLines.getValue());
                Payslip payslip = calculator.calculatePayslip(employeeId, weeklyHours);
                payslips.add(payslip);
            } catch (EmployeeNotFoundException | PayrollValidationException e) {
                failures.add(new PayrollFailure(employeeId, e.getMessage()));
            }
        }

        return new PayrollRunReport(payslips, failures);
    }

    private static Map<String, List<PayrollEntry>> groupByEmployee(List<PayrollEntry> entries) {
        Objects.requireNonNull(entries, "entries must not be null");
        Map<String, List<PayrollEntry>> linesByEmployee = new LinkedHashMap<>();

        for (PayrollEntry entry : entries) {
            Objects.requireNonNull(entry, "entries must not contain null elements");
            linesByEmployee.computeIfAbsent(entry.employeeId(), key -> new ArrayList<>()).add(entry);
        }

        return linesByEmployee;
    }

    private static HoursWorked combineLines(String employeeId, List<PayrollEntry> lines) {
        if (lines.isEmpty()) {
            throw new IllegalStateException("Employee lines must not be empty");
        }
        double hourlyRate = lines.get(0).hourlyRate();
        requireConsistentRate(employeeId, lines, hourlyRate);
        double totalHours = 0;

        for (PayrollEntry line : lines) {
            HoursWorked lineHours = new HoursWorked(line.hours(), line.hourlyRate());
            totalHours = totalHours + lineHours.hours();
        }

        return new HoursWorked(totalHours, hourlyRate);
    }

    private static void requireConsistentRate(String employeeId, List<PayrollEntry> lines,
            double hourlyRate) {
        for (PayrollEntry line : lines) {
            if (line.hourlyRate() != hourlyRate) {
                throw new PayrollValidationException(
                        "Lines for " + employeeId + " have different hourly rates");
            }
        }
    }
}
