package exception;

public class EmployeeNotFoundException extends RuntimeException {

    public EmployeeNotFoundException(String employeeId) {
        super("No employee with ID '" + employeeId + "'");
    }
}
