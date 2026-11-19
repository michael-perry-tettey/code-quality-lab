package pay;

import domain.HoursWorked;

public final class GrossPayCalculator {

    private static final double STANDARD_WEEK_HOURS = 40;
    private static final double OVERTIME_MULTIPLIER = 1.5;

    public double grossPayFor(HoursWorked hoursWorked) {
        return basicPayFor(hoursWorked) + overtimePayFor(hoursWorked);
    }

    public double basicPayFor(HoursWorked hoursWorked) {
        double standardHours = Math.min(hoursWorked.hours(), STANDARD_WEEK_HOURS);
        return standardHours * hoursWorked.hourlyRate();
    }

    public double overtimePayFor(HoursWorked hoursWorked) {
        if (hoursWorked.hours() <= STANDARD_WEEK_HOURS) {
            return 0;
        }
        double overtimeHours = hoursWorked.hours() - STANDARD_WEEK_HOURS;
        return overtimeHours * hoursWorked.hourlyRate() * OVERTIME_MULTIPLIER;
    }
}
