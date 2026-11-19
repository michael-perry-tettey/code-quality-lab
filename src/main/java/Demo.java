import java.util.List;

import domain.Employee;
import domain.PayrollCalculator;
import domain.Payslip;
import pay.GrossPayCalculator;
import report.PayrollEntry;
import report.PayrollFailure;
import report.PayrollRun;
import report.PayrollRunReport;
import repository.InMemoryEmployeeRepository;
import tax.BandedTaxPolicy;

public class Demo {
    public static void main(String[] args) {
        InMemoryEmployeeRepository employees = new InMemoryEmployeeRepository(List.of(
                new Employee("e1", "Efua Sarpong"),
                new Employee("e2", "Kwame Owusu"),
                new Employee("e3", "Ama Owusu")));
        PayrollCalculator calculator = new PayrollCalculator(employees, new GrossPayCalculator(),
                BandedTaxPolicy.rosewoodBands());
        PayrollRun payrollRun = new PayrollRun(calculator);

        List<PayrollEntry> week = List.of(
                new PayrollEntry("e1", 30, 20),
                new PayrollEntry("e2", 50, 30),
                new PayrollEntry("unknown", 40, 20),
                new PayrollEntry("e1", 30, 20),
                new PayrollEntry("e3", -5, 30));

        PayrollRunReport report = payrollRun.run(week);
        printReport(report);
    }

    private static void printReport(PayrollRunReport report) {
        System.out.println("Payslips:");

        for (Payslip payslip : report.payslips()) {
            System.out.println("  " + payslip.employee().name()
                    + ": basic " + payslip.basicPay()
                    + ", overtime " + payslip.overtimePay()
                    + ", tax " + payslip.tax()
                    + ", net " + payslip.netPay());
        }

        System.out.println("Not paid:");

        for (PayrollFailure failure : report.failures()) {
            System.out.println("  " + failure.employeeId() + ": " + failure.reason());
        }

        System.out.println("Total net pay: " + report.totalNetPay()
                + " (" + report.payslips().size() + " paid, "
                + report.failures().size() + " not paid)");
    }
}
