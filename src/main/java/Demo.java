import domain.Employee;
import domain.HoursWorked;
import domain.PayrollCalculator;
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

        double efuaPay = calculator.calculateNetPay("e1", new HoursWorked(40, 20));
        System.out.println("Result e1, 40h @ 20     -> " + efuaPay);

        double kwamePay = calculator.calculateNetPay("e2", new HoursWorked(50, 30));
        System.out.println("Result e2, 50h @ 30     -> " + kwamePay);

        try {
            double unknownPay = calculator.calculateNetPay("unknown", new HoursWorked(40, 20));
            System.out.println("Result unknown employee -> " + unknownPay);
        } catch (EmployeeNotFoundException e) {
            System.out.println("Result unknown employee -> not paid: " + e.getMessage());
        }
    }
}
