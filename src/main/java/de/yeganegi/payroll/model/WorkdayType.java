package de.yeganegi.payroll.model;

import java.math.BigDecimal;

public enum WorkdayType {
    NONE(new BigDecimal("0.0")),
    HALF_DAY(new BigDecimal("0.5")),
    FULL_DAY(new BigDecimal("1.0"));

    private final BigDecimal value;

    WorkdayType(BigDecimal value) {
        this.value = value;
    }

    public BigDecimal getValue() {
        return value;
    }
}
