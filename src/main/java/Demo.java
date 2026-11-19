import domain.Employee;
import domain.HoursWorked;
import domain.PayrollCalculator;
import domain.Payslip;
import exception.EmployeeNotFoundException;
import java.util.List;
import pay.GrossPayCalculator;
import repository.InMemoryEmployeeRepository;
import tax.BandedTaxPolicy;

public class Demo {
    public static void main(String[] args) {
        InMemoryEmployeeRepository employees = new InMemoryEmployeeRepository(List.of(
                new Employee("e1", "Efua Sarpong"),
                new Employee("e2", "Kwame Owusu")));
        PayrollCalculator calculator = new PayrollCalculator(employees, new GrossPayCalculator(),
                BandedTaxPolicy.rosewoodBands());

        Payslip efuaPayslip = calculator.calculatePayslip("e1", new HoursWorked(40, 20));
        printPayslip(efuaPayslip);

        Payslip kwamePayslip = calculator.calculatePayslip("e2", new HoursWorked(50, 30));
        printPayslip(kwamePayslip);

        HoursWorked unknownHours = new HoursWorked(40, 20);
        try {
            Payslip unknownPayslip = calculator.calculatePayslip("unknown", unknownHours);
            printPayslip(unknownPayslip);
        } catch (EmployeeNotFoundException e) {
            System.out.println("Not paid: " + e.getMessage());
        }
    }

    private static void printPayslip(Payslip payslip) {
        System.out.println(payslip.employee().getName()
                + ": basic " + payslip.basicPay()
                + ", overtime " + payslip.overtimePay()
                + ", tax " + payslip.tax()
                + ", net " + payslip.netPay());
    }
}
