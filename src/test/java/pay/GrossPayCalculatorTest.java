package pay;

import static org.junit.jupiter.api.Assertions.assertEquals;

import domain.HoursWorked;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class GrossPayCalculatorTest {

    private static final double PESEWA_PRECISION = 0.0001;

    private final GrossPayCalculator calculator = new GrossPayCalculator();

    @Test
    @DisplayName("Pays every hour at the basic rate up to 40 hours")
    void paysEveryHourAtTheBasicRateUpToFortyHours() {
        double grossPay = calculator.grossPayFor(new HoursWorked(40, 20));
        assertEquals(800.00, grossPay, PESEWA_PRECISION);
    }

    @Test
    @DisplayName("Pays no overtime at exactly 40 hours")
    void paysNoOvertimeAtExactlyFortyHours() {
        double overtimePay = calculator.overtimePayFor(new HoursWorked(40, 20));
        assertEquals(0.00, overtimePay, PESEWA_PRECISION);
    }

    @Test
    @DisplayName("Caps basic pay at 40 hours")
    void capsBasicPayAtFortyHours() {
        double basicPay = calculator.basicPayFor(new HoursWorked(50, 30));
        assertEquals(1200.00, basicPay, PESEWA_PRECISION);
    }

    @Test
    @DisplayName("Pays hours over 40 at time and a half")
    void paysHoursOverFortyAtTimeAndAHalf() {
        double overtimePay = calculator.overtimePayFor(new HoursWorked(50, 30));
        assertEquals(450.00, overtimePay, PESEWA_PRECISION);
    }

    @Test
    @DisplayName("Adds basic and overtime pay into gross pay")
    void addsBasicAndOvertimePayIntoGrossPay() {
        double grossPay = calculator.grossPayFor(new HoursWorked(50, 30));
        assertEquals(1650.00, grossPay, PESEWA_PRECISION);
    }
}
