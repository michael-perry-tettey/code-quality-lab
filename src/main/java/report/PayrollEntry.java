package report;

/** One line of a week's payroll input, checked only when the run pays it. */
public record PayrollEntry(String employeeId, double hours, double hourlyRate) {
}
