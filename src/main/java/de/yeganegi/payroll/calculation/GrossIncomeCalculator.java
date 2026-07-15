package de.yeganegi.payroll.calculation;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class GrossIncomeCalculator {

    public BigDecimal calculate(
            BigDecimal workingHours,
            BigDecimal hourlyWage
    ) {
        if (workingHours == null) {
            throw new IllegalArgumentException(
                    "Arbeitsstunden dürfen nicht null sein."
            );
        }

        if (hourlyWage == null) {
            throw new IllegalArgumentException(
                    "Stundenlohn darf nicht null sein."
            );
        }

        if (workingHours.signum() < 0) {
            throw new IllegalArgumentException(
                    "Arbeitsstunden dürfen nicht negativ sein."
            );
        }

        if (hourlyWage.signum() < 0) {
            throw new IllegalArgumentException(
                    "Stundenlohn darf nicht negativ sein."
            );
        }

        return workingHours
                .multiply(hourlyWage)
                .setScale(2, RoundingMode.HALF_UP);
    }
}