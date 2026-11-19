package domain;

import static validation.InputValidator.requireText;

import java.util.Objects;
import repository.EmployeeRepository;

public class PayrollCalculator {

    private final EmployeeRepository repository;

    public PayrollCalculator(EmployeeRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public double calculateNetPay(String employeeId, HoursWorked hoursWorked) {
        requireText(employeeId, "Employee ID");
        Employee employee = repository.getById(employeeId);
        double hours = hoursWorked.hours();
        double hourlyRate = hoursWorked.hourlyRate();

        double gross = hours * hourlyRate;

        // overtime: extra hours beyond 40 are paid at time-and-a-half
        if (hours > 40) {
            gross = gross + (hours - 40) * hourlyRate * 0.5;
        }

        // tax band, worked out from gross pay
        double taxRate;
        if (gross < 1000) {
            taxRate = 0.05;      // LOW
        } else if (gross < 5000) {
            taxRate = 0.15;      // MEDIUM
        } else {
            taxRate = 0.25;      // HIGH
        }

        double net = gross - (gross * taxRate);
        System.out.println("Net pay for " + employee.getName() + ": GHS " + net);
        return net;
    }
}
