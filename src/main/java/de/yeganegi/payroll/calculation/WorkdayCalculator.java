package de.yeganegi.payroll.calculation;

import de.yeganegi.payroll.model.WorkdayType;

import java.math.BigDecimal;

public final class WorkdayCalculator {

    private static final BigDecimal HALF_DAY_LIMIT = new BigDecimal("4.00");

    public WorkdayType calculate(BigDecimal workingHours) {
        if (workingHours == null) {
            throw new IllegalArgumentException("Arbeitsstunden dürfen nicht null sein.");
        }

        if (workingHours.signum() < 0) {
            throw new IllegalArgumentException("Arbeitsstunden dürfen nicht negativ sein.");
        }

        if (workingHours.signum() == 0) {
            return WorkdayType.NONE;
        }

        if (workingHours.compareTo(HALF_DAY_LIMIT) <= 0) {
            return WorkdayType.HALF_DAY;
        }

        return WorkdayType.FULL_DAY;
    }
}
