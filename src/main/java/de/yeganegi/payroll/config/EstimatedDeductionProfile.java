package de.yeganegi.payroll.config;

import java.math.BigDecimal;
import java.util.Objects;

public record EstimatedDeductionProfile(
        BigDecimal wageTaxRate,
        BigDecimal healthInsuranceRate,
        BigDecimal pensionInsuranceRate,
        BigDecimal nursingCareInsuranceRate,
        BigDecimal unemploymentInsuranceRate
) {

    public EstimatedDeductionProfile {
        validateRate(wageTaxRate, "Lohnsteuersatz");
        validateRate(healthInsuranceRate, "Krankenversicherungssatz");
        validateRate(pensionInsuranceRate, "Rentenversicherungssatz");
        validateRate(nursingCareInsuranceRate, "Pflegeversicherungssatz");
        validateRate(unemploymentInsuranceRate, "Arbeitslosenversicherungssatz");
    }

    private static void validateRate(
            BigDecimal rate,
            String fieldName
    ) {
        Objects.requireNonNull(
                rate,
                fieldName + " darf nicht null sein."
        );

        if (rate.signum() < 0
                || rate.compareTo(BigDecimal.ONE) > 0) {
            throw new IllegalArgumentException(
                    fieldName + " muss zwischen 0 und 1 liegen."
            );
        }
    }
}
