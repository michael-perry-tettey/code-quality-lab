package tax;

import static validation.InputValidator.requireBetween;

public record TaxBand(double lowerLimit, double rate) {

    public TaxBand {
        requireBetween(lowerLimit, 0, Double.MAX_VALUE, "Band lower limit");
        requireBetween(rate, 0, 1, "Tax rate");
    }
}
