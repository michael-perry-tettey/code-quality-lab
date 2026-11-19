package validation;

public final class InputValidator {

    private InputValidator() {
    }

    public static String requireText(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new PayrollValidationException(name + " must not be blank");
        }

        return value;
    }

    public static double requireBetween(double value, double min, double max, String name) {
        if (Double.isNaN(value) || value < min || value > max) {
            throw new PayrollValidationException(
                    name + " must be between " + min + " and " + max + ", got " + value);
        }

        return value;
    }

    public static double requirePositive(double value, String name) {
        if (Double.isNaN(value) || value <= 0) {
            throw new PayrollValidationException(name + " must be more than 0, got " + value);
        }

        return value;
    }
}
