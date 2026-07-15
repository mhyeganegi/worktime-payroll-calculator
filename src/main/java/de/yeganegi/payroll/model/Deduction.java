package de.yeganegi.payroll.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public class Deduction {

    private final DeductionType type;
    private final BigDecimal amount;

    public Deduction(
            DeductionType type,
            BigDecimal amount
    ) {
        this.type = Objects.requireNonNull(
                type,
                "Abzugsart darf nicht null sein."
        );

        Objects.requireNonNull(
                amount,
                "Abzugsbetrag darf nicht null sein."
        );

        if (amount.signum() < 0) {
            throw new IllegalArgumentException(
                    "Abzugsbetrag darf nicht negativ sein."
            );
        }

        this.amount = amount.setScale(
                2,
                RoundingMode.HALF_UP
        );
    }

    public DeductionType getType() {
        return type;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}
