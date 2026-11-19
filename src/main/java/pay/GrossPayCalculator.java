package pay;

import domain.HoursWorked;
import java.math.BigDecimal;

public final class GrossPayCalculator {

    private static final double STANDARD_WEEK_HOURS = 40;
    private static final BigDecimal OVERTIME_MULTIPLIER = new BigDecimal("1.5");

    public BigDecimal basicPayFor(HoursWorked hoursWorked) {
        double standardHours = Math.min(hoursWorked.hours(), STANDARD_WEEK_HOURS);
        BigDecimal hourlyRate = BigDecimal.valueOf(hoursWorked.hourlyRate());
        return BigDecimal.valueOf(standardHours).multiply(hourlyRate);
    }

    public BigDecimal overtimePayFor(HoursWorked hoursWorked) {
        if (hoursWorked.hours() <= STANDARD_WEEK_HOURS) {
            return BigDecimal.ZERO;
        }
        double overtimeHours = hoursWorked.hours() - STANDARD_WEEK_HOURS;
        BigDecimal hourlyRate = BigDecimal.valueOf(hoursWorked.hourlyRate());
        return BigDecimal.valueOf(overtimeHours).multiply(hourlyRate).multiply(OVERTIME_MULTIPLIER);
    }
}
