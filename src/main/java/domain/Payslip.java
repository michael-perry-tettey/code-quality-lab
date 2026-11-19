package domain;

public record Payslip(Employee employee, double basicPay, double overtimePay, double tax,
        double netPay) {

    public double grossPay() {
        return basicPay + overtimePay;
    }
}
