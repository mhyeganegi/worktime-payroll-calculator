package de.yeganegi.payroll.config;

import de.yeganegi.payroll.model.EmploymentType;

import java.math.BigDecimal;
import java.util.Objects;

public final class EstimatedProfileFactory {

    private EstimatedProfileFactory() {
    }

    public static EstimatedDeductionProfile create(
            EmploymentType employmentType
    ) {
        Objects.requireNonNull(
                employmentType,
                "Beschäftigungsart darf nicht null sein."
        );

        return switch (employmentType) {
            case WORKING_STUDENT ->
                    new EstimatedDeductionProfile(
                            new BigDecimal("0.05"),
                            BigDecimal.ZERO,
                            new BigDecimal("0.093"),
                            BigDecimal.ZERO,
                            BigDecimal.ZERO
                    );

            case MINI_JOB ->
                    new EstimatedDeductionProfile(
                            BigDecimal.ZERO,
                            BigDecimal.ZERO,
                            BigDecimal.ZERO,
                            BigDecimal.ZERO,
                            BigDecimal.ZERO
                    );

            case MIDI_JOB ->
                    new EstimatedDeductionProfile(
                            new BigDecimal("0.05"),
                            new BigDecimal("0.08"),
                            new BigDecimal("0.07"),
                            new BigDecimal("0.015"),
                            new BigDecimal("0.01")
                    );

            case PART_TIME, FULL_TIME ->
                    new EstimatedDeductionProfile(
                            new BigDecimal("0.10"),
                            new BigDecimal("0.08"),
                            new BigDecimal("0.093"),
                            new BigDecimal("0.018"),
                            new BigDecimal("0.013")
                    );
        };
    }
}
