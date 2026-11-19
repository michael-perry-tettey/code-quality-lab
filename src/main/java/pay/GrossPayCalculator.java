package pay;

import domain.HoursWorked;
import java.math.BigDecimal;

public final class GrossPayCalculator {

    private static final double STANDARD_WEEK_HOURS = 40;
    private static final BigDecimal BASIC_MULTIPLIER = BigDecimal.ONE;
    private static final BigDecimal OVERTIME_MULTIPLIER = new BigDecimal("1.5");

    public BigDecimal basicPayFor(HoursWorked hoursWorked) {
        double basicHours = Math.min(hoursWorked.hours(), STANDARD_WEEK_HOURS);
        return payFor(basicHours, hoursWorked.hourlyRate(), BASIC_MULTIPLIER);
    }

    public BigDecimal overtimePayFor(HoursWorked hoursWorked) {
        double overtimeHours = Math.max(hoursWorked.hours() - STANDARD_WEEK_HOURS, 0);
        return payFor(overtimeHours, hoursWorked.hourlyRate(), OVERTIME_MULTIPLIER);
    }

    private static BigDecimal payFor(double hours, double hourlyRate, BigDecimal multiplier) {
        BigDecimal rate = BigDecimal.valueOf(hourlyRate);
        return BigDecimal.valueOf(hours).multiply(rate).multiply(multiplier);
    }
}
