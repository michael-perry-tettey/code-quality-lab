package validation;

/** An invalid value supplied to the payroll workflow. */
public final class PayrollValidationException extends IllegalArgumentException {

    public PayrollValidationException(String message) {
        super(message);
    }
}
