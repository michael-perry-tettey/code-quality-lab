package tax;

import java.math.BigDecimal;
import java.util.List;

/** Taxes all of gross pay at the rate of the highest band it reaches. */
public final class BandedTaxPolicy implements TaxPolicy {

    private static final List<TaxBand> ROSEWOOD_BANDS = List.of(
            new TaxBand(0, 0.05),
            new TaxBand(1000, 0.15),
            new TaxBand(5000, 0.25));

    private final List<TaxBand> bands;

    public BandedTaxPolicy(List<TaxBand> bands) {
        checkBandsStartAtZeroAndRise(bands);
        this.bands = List.copyOf(bands);
    }

    public static BandedTaxPolicy rosewoodBands() {
        return new BandedTaxPolicy(ROSEWOOD_BANDS);
    }

    @Override
    public BigDecimal taxFor(BigDecimal grossPay) {
        return grossPay.multiply(rateFor(grossPay));
    }

    private BigDecimal rateFor(BigDecimal grossPay) {
        BigDecimal rate = BigDecimal.ZERO;

        for (TaxBand band : bands) {
            BigDecimal lowerLimit = BigDecimal.valueOf(band.lowerLimit());
            if (grossPay.compareTo(lowerLimit) >= 0) {
                rate = BigDecimal.valueOf(band.rate());
            }
        }

        return rate;
    }

    private static void checkBandsStartAtZeroAndRise(List<TaxBand> bands) {
        if (bands.isEmpty() || bands.get(0).lowerLimit() != 0) {
            throw new IllegalArgumentException("Tax bands must start at 0");
        }

        for (int i = 1; i < bands.size(); i++) {
            if (bands.get(i).lowerLimit() <= bands.get(i - 1).lowerLimit()) {
                throw new IllegalArgumentException("Tax band limits must rise in order");
            }
        }
    }
}
