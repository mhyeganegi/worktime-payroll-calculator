package de.yeganegi.payroll.model;

import java.math.BigDecimal;
import java.util.Objects;

public record PayrollProfile(
        long employeeId,
        TaxClass taxClass,
        BigDecimal healthInsuranceRate,
        boolean churchTaxEnabled
) {

    public PayrollProfile {
        if (employeeId <= 0) {
            throw new IllegalArgumentException(
                    "Mitarbeiter-ID muss größer als 0 sein."
            );
        }

        Objects.requireNonNull(
                taxClass,
                "Steuerklasse darf nicht null sein."
        );

        Objects.requireNonNull(
                healthInsuranceRate,
                "Krankenversicherungssatz darf nicht null sein."
        );

        if (healthInsuranceRate.signum() < 0
                || healthInsuranceRate.compareTo(BigDecimal.ONE) > 0) {
            throw new IllegalArgumentException(
                    "Krankenversicherungssatz muss zwischen 0 und 1 liegen."
            );
        }
    }
}
