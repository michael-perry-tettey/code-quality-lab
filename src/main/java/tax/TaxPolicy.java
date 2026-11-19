package tax;

import java.math.BigDecimal;

public interface TaxPolicy {

    BigDecimal taxFor(BigDecimal grossPay);
}
