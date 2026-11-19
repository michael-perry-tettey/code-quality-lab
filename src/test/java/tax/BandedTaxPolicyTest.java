package tax;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class BandedTaxPolicyTest {

    private final BandedTaxPolicy policy = BandedTaxPolicy.rosewoodBands();

    @ParameterizedTest(name = "Gross {0} is taxed {1}")
    @DisplayName("Taxes all of gross pay at the rate of the band it reaches")
    @CsvSource({
        "0, 0",
        "999.99, 49.9995",
        "1000, 150",
        "4999.99, 749.9985",
        "5000, 1250"
    })
    void taxesAllOfGrossPayAtTheRateOfTheBandItReaches(BigDecimal grossPay,
            BigDecimal expectedTax) {
        BigDecimal tax = policy.taxFor(grossPay);
        assertEquals(0, expectedTax.compareTo(tax), "expected " + expectedTax + " but was " + tax);
    }

    @Test
    @DisplayName("Rejects bands that do not start at 0")
    void rejectsBandsThatDoNotStartAtZero() {
        List<TaxBand> bands = List.of(new TaxBand(100, 0.05));
        assertThrows(IllegalArgumentException.class, () -> new BandedTaxPolicy(bands));
    }

    @Test
    @DisplayName("Rejects bands whose limits do not rise")
    void rejectsBandsWhoseLimitsDoNotRise() {
        List<TaxBand> bands = List.of(new TaxBand(0, 0.05), new TaxBand(0, 0.15));
        assertThrows(IllegalArgumentException.class, () -> new BandedTaxPolicy(bands));
    }

    @Test
    @DisplayName("Rejects an empty list of bands")
    void rejectsAnEmptyListOfBands() {
        List<TaxBand> bands = List.of();
        assertThrows(IllegalArgumentException.class, () -> new BandedTaxPolicy(bands));
    }

    @Test
    @DisplayName("Rejects a tax rate above 100%")
    void rejectsATaxRateAboveOneHundredPercent() {
        assertThrows(IllegalArgumentException.class, () -> new TaxBand(0, 1.5));
    }
}
