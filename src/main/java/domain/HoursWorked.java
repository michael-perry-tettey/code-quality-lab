package domain;

import static validation.InputValidator.requireBetween;
import static validation.InputValidator.requirePositive;

public record HoursWorked(double hours, double hourlyRate) {

    private static final double MAX_HOURS_IN_WEEK = 168;

    public HoursWorked {
        requireBetween(hours, 0, MAX_HOURS_IN_WEEK, "Hours worked");
        requirePositive(hourlyRate, "Hourly rate");
    }
}
