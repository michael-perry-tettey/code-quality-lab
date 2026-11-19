import domain.Employee;
import domain.PayrollCalculator;
import exception.EmployeeNotFoundException;
import java.util.List;
import repository.InMemoryEmployeeRepository;

public class Demo {
    public static void main(String[] args) {
        InMemoryEmployeeRepository employees = new InMemoryEmployeeRepository(List.of(
                new Employee("e1", "Efua Sarpong"),
                new Employee("e2", "Kwame Owusu")));
        PayrollCalculator calculator = new PayrollCalculator(employees);

        System.out.println("Result e1, 40h @ 20     -> " + calculator.calculateNetPay("e1", 40, 20));
        System.out.println("Result e2, 50h @ 30     -> " + calculator.calculateNetPay("e2", 50, 30));
        try {
            System.out.println("Result unknown employee -> " + calculator.calculateNetPay("unknown", 40, 20));
        } catch (EmployeeNotFoundException e) {
            System.out.println("Result unknown employee -> not paid: " + e.getMessage());
        }
    }
}
