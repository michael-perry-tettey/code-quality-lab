package domain;

import java.math.BigDecimal;

public record Payslip(Employee employee, BigDecimal basicPay, BigDecimal overtimePay,
        BigDecimal tax, BigDecimal netPay) {

    public BigDecimal grossPay() {
        return basicPay.add(overtimePay);
    }
}
