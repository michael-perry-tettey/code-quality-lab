package report;

import domain.Payslip;
import java.math.BigDecimal;
import java.util.List;

public record PayrollRunReport(List<Payslip> payslips, List<PayrollFailure> failures) {

    public PayrollRunReport {
        payslips = List.copyOf(payslips);
        failures = List.copyOf(failures);
    }

    public BigDecimal totalNetPay() {
        BigDecimal total = new BigDecimal("0.00");
        for (Payslip payslip : payslips) {
            total = total.add(payslip.netPay());
        }
        return total;
    }
}
