package pay;

import static org.junit.jupiter.api.Assertions.assertEquals;

import domain.HoursWorked;
import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class GrossPayCalculatorTest {

    private final GrossPayCalculator calculator = new GrossPayCalculator();

    @Test
    @DisplayName("Pays every hour at the basic rate up to 40 hours")
    void paysEveryHourAtTheBasicRateUpToFortyHours() {
        BigDecimal basicPay = calculator.basicPayFor(new HoursWorked(40, 20));
        assertAmount("800", basicPay);
    }

    @Test
    @DisplayName("Pays no overtime at exactly 40 hours")
    void paysNoOvertimeAtExactlyFortyHours() {
        BigDecimal overtimePay = calculator.overtimePayFor(new HoursWorked(40, 20));
        assertAmount("0", overtimePay);
    }

    @Test
    @DisplayName("Caps basic pay at 40 hours")
    void capsBasicPayAtFortyHours() {
        BigDecimal basicPay = calculator.basicPayFor(new HoursWorked(50, 30));
        assertAmount("1200", basicPay);
    }

    @Test
    @DisplayName("Pays hours over 40 at time and a half")
    void paysHoursOverFortyAtTimeAndAHalf() {
        BigDecimal overtimePay = calculator.overtimePayFor(new HoursWorked(50, 30));
        assertAmount("450", overtimePay);
    }

    private static void assertAmount(String expected, BigDecimal actual) {
        assertEquals(0, new BigDecimal(expected).compareTo(actual),
                "expected " + expected + " but was " + actual);
    }
}
